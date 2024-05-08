package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @projectName: super-takeout
 * @package: com.sky.controller.admin
 * @className: DishController
 * @author: 749291
 * @description: TODO
 * @date: 4/27/2024 15:33
 * @version: 1.0
 */

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "后台菜品管理接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 新增菜品
     */
    @ApiOperation("新增菜品")
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);

        dishService.saveWithFlavor(dishDTO);


        // 清空redis中的菜品列表
        cleanCache("dishList_" + dishDTO.getCategoryId());

        return Result.success();
    }


    /**
     * 菜品分页查询
     */
    @ApiOperation("菜品分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询：{}", dishPageQueryDTO);

        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     */
    @ApiOperation("批量删除菜品")
    @DeleteMapping
    public Result delete(@RequestParam("ids") Long[] ids) {
        log.info("批量删除菜品：{}", ids);

        dishService.deleteBatch(ids);

        cleanCache("dishList_*");

        return Result.success();
    }


    /**
     * 修改菜品
     */
    @ApiOperation("修改菜品")
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO);


        dishService.updateWithFlavor(dishDTO);

        // 清空redis中的菜品列表
        cleanCache("dishList_*");

        return Result.success();
    }


    /**
     * 根据菜品id查询菜品
     */
    @ApiOperation("根据菜品id查询菜品")
    @GetMapping("/{id}")
    public Result<DishVO> findById(@PathVariable("id") Long id) {
        log.info("根据菜品id查询菜品：{}", id);

        DishVO dishVo = dishService.findById(id);

        return Result.success(dishVo);
    }

    /**
     * 启售/停售菜品
     */
    @ApiOperation("启售/停售菜品")
    @PostMapping("/status/{status}")
    public Result updateStatus(@RequestParam("id") Long id, @PathVariable("status") Integer status) {
        log.info("启售/停售菜品：id={},status={}", id, status);

        dishService.updateStatus(id, status);

        // 清空redis中的菜品列表
        cleanCache("dishList_*");

        return Result.success();
    }


    /**
     * 根据分类id查询菜品
     */
    @ApiOperation("根据分类id查询菜品")
    @GetMapping("/list")
    public Result listByCategoryId(@RequestParam("categoryId") Long categoryId) {
        log.info("根据分类id查询菜品：{}", categoryId);

        return Result.success(dishService.listByCategoryId(categoryId));
    }



    /**
     * 清理redis缓存
     */
    private void cleanCache(String pattern) {
        redisTemplate.keys(pattern).forEach(key -> {
            redisTemplate.delete(key);
        });
    }
}
