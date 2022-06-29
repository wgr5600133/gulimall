package com.atguigu.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> entityList = baseMapper.selectList(null);
        List<CategoryEntity> collect = entityList.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .map(memu -> {
                    memu.setChildren(getChildren(memu, entityList));
                    return memu;
                })
                .sorted((menu1,menu2)->(menu1.getSort()==null?0:menu1.getSort())-(menu2.getSort()==null?0:menu2.getSort()))
                .collect(Collectors.toList());
        return collect;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        baseMapper.deleteBatchIds(asList);
    }

    private List<CategoryEntity> getChildren(CategoryEntity root,List<CategoryEntity> all){
        List<CategoryEntity> collect = all.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid().equals(root.getCatId()))
                .map(categoryEntity -> {
                    categoryEntity.setChildren(getChildren(categoryEntity,all));
                    return categoryEntity;
                })
                .sorted((menu1,menu2)->(menu1.getSort()==null?0:menu1.getSort())-(menu2.getSort()==null?0:menu2.getSort()))
                .collect(Collectors.toList());
        return collect;
    }

}