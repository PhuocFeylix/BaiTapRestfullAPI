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

    @GetMapping
    public ResponseEntity<Response> getAll() {
        return ResponseEntity.ok(new Response(true, "Lấy danh sách danh mục thành công", categoryService.findAll()));
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
        if (categoryService.findByCategoryName(category.getCategoryName().trim()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(new Response(false, "Tên danh mục đã tồn tại", null));
        }

        category.setCategoryId(null);
        category.setCategoryName(category.getCategoryName().trim());
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

        return ResponseEntity.ok(new Response(true, "Cập nhật danh mục thành công", categoryService.save(category)));
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
}
