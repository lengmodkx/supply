package com.art1001.supply.entity.product;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author heshaohua
 * @since 2019-08-31
 */
@Data
@TableName("prm_product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Integer id;

    /**
     * 产品名称
     */
    private String name;

    /**
     * 产品单价
     */
    private BigDecimal price;

    /**
     * 上传时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Product{" +
        "id=" + id +
        ", name=" + name +
        ", price=" + price +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
