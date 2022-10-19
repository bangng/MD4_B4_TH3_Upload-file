package rikkei.academy.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import rikkei.academy.model.Product;
import rikkei.academy.model.ProductForm;
import rikkei.academy.service.IProductService;
import rikkei.academy.service.ProductServiceIMPL;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ProductController {
    private final IProductService productService = new ProductServiceIMPL();

    @GetMapping(value = {"/", "/product"})
    public String viewIndex(Model model) {
        List<Product> productList = productService.findAll();
        model.addAttribute("productList", productList);
        return "/index";
    }

    @GetMapping("/create")
    private ModelAndView showCreateForm() {
        ModelAndView modelAndView = new ModelAndView("/create");
        modelAndView.addObject("productForm", new ProductForm());
        return modelAndView;
    }

    @Value("${upload_file}")
    private String fileUpload;

    @PostMapping("/save")
    public ModelAndView saveProduct(@ModelAttribute ProductForm productForm) {
        MultipartFile multipartFile = productForm.getImage();
        String fileName = multipartFile.getOriginalFilename();
        try {
            FileCopyUtils.copy(productForm.getImage().getBytes(), new File(fileUpload + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Product product = new Product(productForm.getId(), productForm.getName(), productForm.getDescription(), fileName);
        productService.save(product);
        ModelAndView modelAndView = new ModelAndView("/create");
        modelAndView.addObject("productForm", productForm);
        modelAndView.addObject("message", "Created new product successfully !");
        return modelAndView;
    }

}
