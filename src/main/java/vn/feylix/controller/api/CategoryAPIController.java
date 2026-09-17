package vn.feylix.controller.api;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.feylix.entity.Category;
import vn.feylix.model.Response;
import vn.feylix.services.ICategoryService;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryAPIController {

    private final ICategoryService categoryService;

    public CategoryAPIController(ICategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // ================= CURRENT AJAX API =================

    @GetMapping
    public ResponseEntity<Response> getAll() {
        return ResponseEntity.ok(
                new Response(true, "Lấy danh sách danh mục thành công", categoryService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getById(@PathVariable Long id) {
        Optional<Category> category = categoryService.findById(id);
        if (category.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "Không tìm thấy danh mục", null));
        }
        return ResponseEntity.ok(new Response(true, "Lấy danh mục thành công", category.get()));
    }

    @PostMapping
    public ResponseEntity<Response> create(@RequestBody Category category) {
        if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new Response(false, "Tên danh mục không được để trống", null));
        }

        String name = category.getCategoryName().trim();
        if (categoryService.findByCategoryName(name).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(new Response(false, "Tên danh mục đã tồn tại", null));
        }

        category.setCategoryId(null);
        category.setCategoryName(name);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response(true, "Thêm danh mục thành công", categoryService.save(category)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> update(@PathVariable Long id, @RequestBody Category request) {
        Optional<Category> optional = categoryService.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "Không tìm thấy danh mục", null));
        }

        if (request.getCategoryName() == null || request.getCategoryName().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new Response(false, "Tên danh mục không được để trống", null));
        }

        Category category = optional.get();
        category.setCategoryName(request.getCategoryName().trim());
        category.setIcon(request.getIcon());

        return ResponseEntity.ok(
                new Response(true, "Cập nhật danh mục thành công", categoryService.save(category)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> delete(@PathVariable Long id) {
        Optional<Category> category = categoryService.findById(id);
        if (category.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "Không tìm thấy danh mục", null));
        }

        categoryService.deleteById(id);
        return ResponseEntity.ok(new Response(true, "Xóa danh mục thành công", null));
    }

    // ================= API THEO MẪU GIẢNG VIÊN =================
    // Giữ song song với API hiện tại để AJAX đang có không bị hỏng.

    @GetMapping("/teacher")
    public ResponseEntity<Response> getAllTeacherStyle() {
        return ResponseEntity.ok(
                new Response(true, "Thành công", categoryService.findAll()));
    }

    @PostMapping("/teacher/getCategory")
    public ResponseEntity<Response> getCategoryTeacherStyle(@RequestParam Long id) {
        Optional<Category> category = categoryService.findById(id);
        if (category.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "Không tìm thấy danh mục", null));
        }
        return ResponseEntity.ok(new Response(true, "Thành công", category.get()));
    }

    @PostMapping("/teacher/addCategory")
    public ResponseEntity<Response> addCategoryTeacherStyle(
            @RequestParam String categoryName,
            @RequestParam(required = false) String icon) {

        if (categoryName == null || categoryName.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new Response(false, "Tên danh mục không được để trống", null));
        }

        String name = categoryName.trim();
        if (categoryService.findByCategoryName(name).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(new Response(false, "Tên danh mục đã tồn tại", null));
        }

        Category category = new Category();
        category.setCategoryName(name);
        category.setIcon(icon);

        return ResponseEntity.ok(
                new Response(true, "Thành công", categoryService.save(category)));
    }

    @PutMapping("/teacher/updateCategory")
    public ResponseEntity<Response> updateCategoryTeacherStyle(
            @RequestParam Long categoryId,
            @RequestParam String categoryName,
            @RequestParam(required = false) String icon) {

        Optional<Category> optional = categoryService.findById(categoryId);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "Không tìm thấy danh mục", null));
        }

        if (categoryName == null || categoryName.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new Response(false, "Tên danh mục không được để trống", null));
        }

        Category category = optional.get();
        category.setCategoryName(categoryName.trim());
        if (icon != null && !icon.trim().isEmpty()) {
            category.setIcon(icon.trim());
        }

        return ResponseEntity.ok(
                new Response(true, "Thành công", categoryService.save(category)));
    }

    @DeleteMapping("/teacher/deleteCategory")
    public ResponseEntity<Response> deleteCategoryTeacherStyle(@RequestParam Long categoryId) {
        Optional<Category> category = categoryService.findById(categoryId);
        if (category.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response(false, "Không tìm thấy danh mục", null));
        }

        categoryService.deleteById(categoryId);
        return ResponseEntity.ok(new Response(true, "Thành công", null));
    }
}
