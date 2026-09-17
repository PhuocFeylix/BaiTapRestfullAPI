package vn.feylix.controller.api;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import vn.feylix.entity.Category;
import vn.feylix.entity.Product;
import vn.feylix.model.Response;
import vn.feylix.services.ICategoryService;
import vn.feylix.services.IProductService;
import vn.feylix.services.IStorageService;

@RestController
@RequestMapping("/api/product")
public class ProductAPIController {

    private final IProductService productService;
    private final ICategoryService categoryService;
    private final IStorageService storageService;

    public ProductAPIController(IProductService productService,
                                ICategoryService categoryService,
                                IStorageService storageService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.storageService = storageService;
    }

    @GetMapping
    public ResponseEntity<Response> getAllProduct() {
        return ResponseEntity.ok(new Response(true, "Lấy danh sách sản phẩm thành công", productService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getProduct(@PathVariable Long id) {
        Optional<Product> product = productService.findById(id);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "Không tìm thấy sản phẩm", null));
        }
        return ResponseEntity.ok(new Response(true, "Lấy sản phẩm thành công", product.get()));
    }

    @PostMapping("/addProduct")
    public ResponseEntity<Response> addProduct(
            @RequestParam String productName,
            @RequestParam Double unitPrice,
            @RequestParam(defaultValue = "0") Double discount,
            @RequestParam String description,
            @RequestParam Long categoryId,
            @RequestParam(defaultValue = "0") Integer quantity,
            @RequestParam(defaultValue = "1") Short status,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        if (productName == null || productName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new Response(false, "Tên sản phẩm không được để trống", null));
        }
        if (productService.findByProductName(productName.trim()).isPresent()) {
            return ResponseEntity.badRequest().body(new Response(false, "Tên sản phẩm đã tồn tại", null));
        }

        Optional<Category> category = categoryService.findById(categoryId);
        if (category.isEmpty()) {
            return ResponseEntity.badRequest().body(new Response(false, "Danh mục không tồn tại", null));
        }

        Product product = new Product();
        product.setProductName(productName.trim());
        product.setUnitPrice(unitPrice);
        product.setDiscount(discount);
        product.setDescription(description);
        product.setQuantity(quantity);
        product.setStatus(status);
        product.setCategory(category.get());
        product.setCreateDate(new Timestamp(new Date().getTime()));

        saveImage(product, imageFile);
        productService.save(product);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response(true, "Thêm sản phẩm thành công", product));
    }

    @PutMapping("/updateProduct")
    public ResponseEntity<Response> updateProduct(
            @RequestParam Long productId,
            @RequestParam String productName,
            @RequestParam Double unitPrice,
            @RequestParam(defaultValue = "0") Double discount,
            @RequestParam String description,
            @RequestParam Long categoryId,
            @RequestParam(defaultValue = "0") Integer quantity,
            @RequestParam(defaultValue = "1") Short status,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        Optional<Product> optional = productService.findById(productId);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "Không tìm thấy sản phẩm", null));
        }

        Optional<Category> category = categoryService.findById(categoryId);
        if (category.isEmpty()) {
            return ResponseEntity.badRequest().body(new Response(false, "Danh mục không tồn tại", null));
        }

        Product product = optional.get();
        product.setProductName(productName.trim());
        product.setUnitPrice(unitPrice);
        product.setDiscount(discount);
        product.setDescription(description);
        product.setQuantity(quantity);
        product.setStatus(status);
        product.setCategory(category.get());

        saveImage(product, imageFile);
        productService.save(product);

        return ResponseEntity.ok(new Response(true, "Cập nhật sản phẩm thành công", product));
    }

    @DeleteMapping("/deleteProduct")
    public ResponseEntity<Response> deleteProduct(@RequestParam Long productId) {
        Optional<Product> product = productService.findById(productId);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "Không tìm thấy sản phẩm", null));
        }

        productService.delete(product.get());
        return ResponseEntity.ok(new Response(true, "Xóa sản phẩm thành công", null));
    }

    private void saveImage(Product product, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            String filename = storageService.getSorageFilename(imageFile, UUID.randomUUID().toString());
            product.setImages(filename);
            storageService.store(imageFile, filename);
        }
    }
}
