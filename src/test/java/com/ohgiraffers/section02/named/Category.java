package com.ohgiraffers.section02.named;

import javax.persistence.*;

@Entity(name = "category_section02")
@Table(name = "tbl_category")
@SqlResultSetMapping(
        name = "categoryCountAutoMapping2",
        entities = {@EntityResult(entityClass = Category.class)},   // 엔티티 필드에 있는 애들
        columns = {@ColumnResult(name = "MENU_COUNT")}  // 엔티티 필드에 없는 애들
)
@NamedNativeQueries(
        value = {
                @NamedNativeQuery(
                        name = "Category.menuCountOfCategory",
                        query = "SELECT"
                                + " a.category_code, a.category_name, a.ref_category_code, NVL(v.menu_count, 0) menu_count"
                                + " FROM tbl_category a"
                                + " LEFT JOIN (SELECT COUNT(*) AS menu_count, b.category_code"
                                + "            FROM tbl_menu b"
                                + "            GROUP BY B.category_code) v ON (a.category_code = v.category_code)"
                                + " ORDER BY 1",
                        resultSetMapping = "categoryCountAutoMapping2" // 결과를 어떻게 처리할 것인가?
                )
        }
)
public class Category {

    @Id
    private int categoryCode;
    private String categoryName;
    private Integer refCategoryCode;


    public Category() {
    }

    public Category(int categoryCode, String categoryName, Integer refCategoryCode) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.refCategoryCode = refCategoryCode;
    }

    public int getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(int categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getRefCategoryCode() {
        return refCategoryCode;
    }

    public void setRefCategoryCode(Integer refCategoryCode) {
        this.refCategoryCode = refCategoryCode;
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryCode=" + categoryCode +
                ", categoryName='" + categoryName + '\'' +
                ", refCategoryCode=" + refCategoryCode +
                '}';
    }
}
