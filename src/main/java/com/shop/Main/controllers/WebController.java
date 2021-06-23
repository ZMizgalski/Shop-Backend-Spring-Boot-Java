package com.shop.Main.controllers;

import com.shop.Main.models.*;
import com.shop.Main.payload.requests.*;
import com.shop.Main.payload.responses.ProductSearchResponse;
import com.shop.Main.payload.responses.MessageResponse;
import com.shop.Main.repositories.ProductRepo;
import com.shop.Main.repositories.TokenRepo;
import com.shop.Main.repositories.UserRepo;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@CrossOrigin(value = "*", maxAge = 3600)
@RequestMapping("/api/staff")
public class WebController {

    @Autowired
    UserRepo userRepo;

    @Autowired
    TokenRepo tokenRepo;

    @Autowired
    ProductRepo productRepo;

    @Autowired
    MailService mailService;

    @PostMapping("/forgotPassword")
    public ResponseEntity<?> resetPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest, HttpServletRequest request) {

        val resetPasswordEmail = forgotPasswordRequest.getEmail();

        if (!userRepo.existsByEmail(resetPasswordEmail)) {
            MessageResponse messageResponse = new MessageResponse("User not found");
            return ResponseEntity.badRequest().body(messageResponse);
        }

        if (tokenRepo.existsByEmail(resetPasswordEmail)) {
            Token token = tokenRepo.findByEmail(resetPasswordEmail);
            tokenRepo.deleteByEmail(token.getEmail());
            log.warn("Token has been regenerated");
        }

        Users userData = userRepo.findByEmail(resetPasswordEmail);
        Token token = new Token();
        token.setEmail(userData.getEmail());
        token.setUsed(false);
        token.setExpiryDate(30);
        token.setToken(UUID.randomUUID().toString());
        tokenRepo.save(token);

        MailRequest mail = new MailRequest();
        //TODO
        mail.setFrom("info@pepisandbox.com");
        mail.setTo(userData.getEmail());
        mail.setSubject("Password reset request");

        Map<String, Object> model = new HashMap<>();
        model.put("token", token);
        model.put("user", userData);
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        model.put("resetUrl", url + "/reset-password?token=" + token.getToken());
        mail.setModel(model);
        mailService.sendEmail(mail);
        return ResponseEntity.ok().body(token);
    }

    private String reduceString(String string, int numberOfCharsToReduce) {
        return string.length() <= numberOfCharsToReduce ? string : string.substring(0, string.length() - numberOfCharsToReduce);
    }


    @PostMapping(value = "/getSearchPredictions")
    public ResponseEntity<?> getSearchPredictions(@RequestBody ProductSearchRequest productSearchRequest) {
        try {
            List<List<Products>> predictions = new ArrayList<>();
            String inputSearch =
                    (productSearchRequest.getInput().trim().isEmpty() ||
                            productSearchRequest.getInput() == null) ?
                            "null" : reduceString(productSearchRequest.getInput(), 2);

            predictions.add(0, productRepo.findByProductNameLike(inputSearch));
            predictions.add(1, productRepo.findByDescriptionLike(inputSearch));
            predictions.add(2, productRepo.findByProductTypeLike(inputSearch));

            List<Products> predictionsReducedList = predictions.parallelStream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            List<ProductSearchResponse> searchResponseList = predictionsReducedList.stream()
                    .map(item -> new ProductSearchResponse(
                            item.getId(),
                            item.getProductName(),
                            item.getDescription(),
                            item.getProductType(),
                            item.getFile())).collect(Collectors.toList());


            Map<String, Object> response = new HashMap<>();
            response.put("predictions", searchResponseList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/getProductById/{id}")
    public ResponseEntity<?> getProduct(@PathVariable String id) {
        try {
            if (!productRepo.existsById(id)) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(productRepo.findById(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(value = "/getProducts")
    public ResponseEntity<?> getProducts(@RequestBody ProductPage productPage) {
        try {
            Pageable pageable = PageRequest.of(productPage.getPageNumber(), productPage.getPageSize());
            Page<Products> pageTuts;

            String productName =
                    (productPage.getProductName().trim().isEmpty() ||
                    productPage.getProductName() == null) ?
                    "null" : reduceString(productPage.getProductName(), 2);
            String productDescription =
                    (productPage.getDescription().trim().isEmpty() ||
                    productPage.getDescription() == null) ?
                    "null" : reduceString(productPage.getDescription(), 2);
            String productContentType =
                    (productPage.getProductType().trim().isEmpty() ||
                    productPage.getProductType() == null) ?
                    "null" : productPage.getProductType();

            if (productContentType.equals("null") && productName.equals("null") & productDescription.equals("null")) {
                pageTuts = productRepo.findAll(pageable);
            } else {
                pageTuts = productRepo.findByProductNameLikeOrDescriptionLikeOrProductType(
                        productName,
                        productDescription,
                        productContentType,
                        pageable
                );
            }

            Map<String, Object> response = new HashMap<>();
            response.put("products", pageTuts.getContent());
            response.put("currentPage", pageTuts.getNumber());
            response.put("totalItems", pageTuts.getTotalElements());
            response.put("totalPages", pageTuts.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(value = "/addProduct")
    public ResponseEntity<?> addProduct(@RequestBody ProductRequest productRequest) {
        if (productRepo.existsByProductName(productRequest.getProductName())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Product name exists!"));
        }
        Products product = new Products();
        product.setProductName(productRequest.getProductName());
        product.setDescription(productRequest.getDescription());
        product.setProductType(productRequest.getProductType().toLowerCase());
        product.setProductOriginPage(productRequest.getProductOriginPage());
        product.setFile(productRequest.getFile());
        productRepo.save(product);
        return ResponseEntity.ok().body(new MessageResponse("Product created"));
    }

    @DeleteMapping("/deleteProduct/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {

        if (!productRepo.existsById(id)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Product not exists"));
        }
        productRepo.deleteById(id);
        return ResponseEntity.ok().body(new MessageResponse("Product has been deleted!"));
    }

}
