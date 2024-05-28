package com.hunt.manager_app.controller;

import com.hunt.manager_app.entity.Product;
import com.hunt.manager_app.payload.UpdateProductPayload;
import com.hunt.manager_app.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.NoSuchElementException;

@RequestMapping("/catalogue/products/{productId:\\d+}")
@RequiredArgsConstructor
@Controller
public class ProductController {

    private final ProductService productService;
//    Механизм интернационализации
    private final MessageSource messageSource;

    @ModelAttribute("product")
    public Product product(@PathVariable("productId") int productId){
        return this.productService.findProduct(productId).orElseThrow(() -> new NoSuchElementException("catalogue.errors.product.not_found"));
    }

    @GetMapping("")
    public String getProduct(@PathVariable("productId") int productId){
        return "catalogue/products/product";
    }

    @GetMapping("edit")
    public String getEditProductEditPage(@PathVariable("productId") int productId){
        return "catalogue/products/edit";
    }

//    Ставим binding = false, чтобы данные не уходили в старую модель, при возникновении ошибок и данные не изменялись
    @PostMapping("edit")
    public String updateProduct(@ModelAttribute(value = "product", binding = false) Product product, @Valid UpdateProductPayload payload, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()){
            model.addAttribute("payload", payload);
            model.addAttribute("errors", bindingResult.getAllErrors().stream().map(ObjectError :: getDefaultMessage).toList());
            return "catalogue/products/edit";
        } else {
            this.productService.updateProduct(product.getId(), payload.title(), payload.details());
            return "redirect:/catalogue/products/%d".formatted(product.getId());
        }
    }

    @PostMapping("delete")
    public String deleteProduct(@ModelAttribute("product") Product product){
        this.productService.deleteProduct(product.getId());
        return "redirect:/catalogue/products/list";
    }

//    Обработчик ошибок, если совершается переход на страницу, котрой нет.
    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException exception, Model model, HttpServletResponse response, Locale locale){
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("error",
                this.messageSource.getMessage(exception.getMessage(), new Object[0], exception.getMessage(), locale));
        return "errors/404";
    }

}
