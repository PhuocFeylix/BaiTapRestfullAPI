package vn.feylix.controller.api;

import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import vn.feylix.entity.Category;
import vn.feylix.model.Response;
import vn.feylix.services.ICategoryService;
import vn.feylix.services.IStorageService;

@RestController
@RequestMapping("/api/category")
public class CategoryAPIController {

    private final ICategoryService categoryService;
    private final IStorageService storageService;

    public CategoryAPIController(ICategoryService categoryService, IStorageService storageService) {
        this.categoryService = categoryService;
        this.storageService = storageService;
    }

    @GetMapping
    public ResponseEntity<Response> getAllCategory() {
        return ResponseEntity.ok(new Response(true, "Thành công", categoryService.findAll()));
    }

    @PostMapping("/getCategory")
    public ResponseEntity<Response> getCategory(@RequestParam Long id) {
        Optional<Category> category = categoryService.findById(id);
        if (category.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "Không tìm thấy Category", null));
        }
        return ResponseEntity.ok(new Response(true, "Thành công", category.get()));
    }

    @PostMapping("/addCategory")
    public ResponseEntity<Response> addCategory(
            @RequestParam String categoryName,
            @RequestParam(value = "iconFile", required = false) MultipartFile iconFile) {

        if (categoryName == null || categoryName.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new Response(false, "Tên Category không được để trống", null));
        }

        String name = categoryName.trim();
        if (categoryService.findByCategoryName(name).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(new Response(false, "Tên Category đã tồn tại", null));
        }

        Category category = new Category();
        category.setCategoryName(name);
        saveIcon(category, iconFile);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response(true, "Thêm Category thành công", categoryService.save(category)));
    }

    @PutMapping("/updateCategory")
    public ResponseEntity<Response> updateCategory(
            @RequestParam Long categoryId,
            @RequestParam String categoryName,
            @RequestParam(value = "iconFile", required = false) MultipartFile iconFile) {

        Optional<Category> optional = categoryService.findById(categoryId);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "Không tìm thấy Category", null));
        }

        if (categoryName == null || categoryName.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new Response(false, "Tên Category không được để trống", null));
        }

        Category category = optional.get();
        category.setCategoryName(categoryName.trim());
        saveIcon(category, iconFile);

        return ResponseEntity.ok(
                new Response(true, "Cập nhật Category thành công", categoryService.save(category)));
    }

    @DeleteMapping("/deleteCategory")
    public ResponseEntity<Response> deleteCategory(@RequestParam Long categoryId) {
        Optional<Category> category = categoryService.findById(categoryId);
        if (category.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "Không tìm thấy Category", null));
        }

        categoryService.deleteById(categoryId);
        return ResponseEntity.ok(new Response(true, "Xóa Category thành công", null));
    }

    private void saveIcon(Category category, MultipartFile iconFile) {
        if (iconFile != null && !iconFile.isEmpty()) {
            String filename = storageService.getSorageFilename(iconFile, UUID.randomUUID().toString());
            category.setIcon(filename);
            storageService.store(iconFile, filename);
        }
    }
}
