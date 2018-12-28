package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.vo.SpecificationVO;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.sellergoods.service.SpecificationService;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;
    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbSpecification> findAll() {
        return specificationMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(SpecificationVO specificationVO) {
        TbSpecification tbSpecification = new TbSpecification();
        tbSpecification.setSpecName(specificationVO.getSpecificationName());
        specificationMapper.insert(tbSpecification);
        specificationVO.setId(tbSpecification.getId());

        List<TbSpecificationOption> optionList = specificationVO.getSpecificationOptionList();
        for (TbSpecificationOption option : optionList) {
            option.setSpecId(tbSpecification.getId());
            specificationOptionMapper.insert(option);

        }
    }


    /**
     * 修改
     */
    @Override
    public void update(SpecificationVO specificationVO) {
        TbSpecification tbSpecification = new TbSpecification();
        tbSpecification.setSpecName(specificationVO.getSpecificationName());
        tbSpecification.setId(specificationVO.getId());
        specificationMapper.updateByPrimaryKey(tbSpecification);

        //先删除相关数据
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(tbSpecification.getId());//根据规格ID查询
        specificationOptionMapper.deleteByExample(example);

        //再从新添加
        List<TbSpecificationOption> optionList = specificationVO.getSpecificationOptionList();
        for (TbSpecificationOption option : optionList) {
            option.setSpecId(tbSpecification.getId());
            specificationOptionMapper.insert(option);

        }
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public SpecificationVO findOne(Long id) {
        SpecificationVO specificationVO = new SpecificationVO();
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        specificationVO.setSpecificationName(tbSpecification.getSpecName());
        specificationVO.setId(tbSpecification.getId());

        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(id);//根据规格ID查询

        List<TbSpecificationOption> optionList = specificationOptionMapper.selectByExample(example);
        specificationVO.setSpecificationOptionList(optionList);

        return specificationVO;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            specificationMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSpecificationExample example = new TbSpecificationExample();
        Criteria criteria = example.createCriteria();

        if (specification != null) {
            if (specification.getSpecName() != null && specification.getSpecName().length() > 0) {
                criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
            }

        }

        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<Map> selectOptionList() {
        return specificationMapper.selectOptionList();
    }
}
