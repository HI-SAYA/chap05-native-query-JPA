package com.ohgiraffers.section01.simple;

import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NativeQueryTests {

    private static EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    @BeforeAll // 모든 테스트 수행하기 전에 딱 한번
    public static void initFactory() {
        entityManagerFactory = Persistence.createEntityManagerFactory("jpatest");
    }

    @BeforeEach //  테스트가 수행 되기 전마다 한번씩
    public void initManager() {
        entityManager = entityManagerFactory.createEntityManager();
    }

    @AfterAll // 모든 테스트 수행하기 전에 딱 한번
    public static void closeFactory() {
        entityManagerFactory.close();
    }

    @AfterEach //  테스트가 수행 되기 전마다 한번씩
    public void closeManager() {
        entityManager.close();
    }


    @Test
    public void 결과_타입을_정의한_네이티브_쿼리_사용_테스트() {
        //given
        int menuCodeParameter = 15;
        //when
        String query = "SELECT MENU_CODE, MENU_NAME, MENU_PRICE, CATEGORY_CODE, ORDERABLE_STATUS" // jpql이 아니라 oracle에서 쓰는 문법 사용
                + " FROM TBL_MENU WHERE MENU_CODE = ?"; // 물음표로 위치홀더 표시
        // String query = "SELECT MENU_CODE, MENU_NAME, MENU_PRICE" +
        //        " FROM TBL_MENU WHERE MENU_CODE = ?";ㅌ
        // ** 일부 컬럼만 불러와서 수행하는 것은 불가능하다. 무조건 전체 컬럼을 조회해야만 수행 가능하다. ??????????????????
        Query nativeQuery = entityManager.createNativeQuery(query, Menu.class)
                // 영속성 컨텍스트에서 관리하는 객체가 맞다. -> 엔티티로 얻어오는 그 결과는 영속성 컨텍스트에서 관리하는 객체라고 말할 수 있다.
                .setParameter(1, menuCodeParameter);   // ** 위치 기반 파라미터만 사용 가능하다. / 1 = 첫번째 ? 위치를 의미!
        Menu foundMenu = (Menu) nativeQuery.getSingleResult(); // 다운캐스팅
        //then
        assertNotNull(foundMenu);
        assertTrue(entityManager.contains(foundMenu)); // 영속성 컨텍스트에 foundMenu가 있느냐?
        System.out.println(foundMenu);
    }


    @Test
    public void 결과_타입을_정의할_수_없는_경우_조회_테스트() {
        //when
        String query = "SELECT MENU_NAME, MENU_PRICE FROM TBL_MENU";
        List<Object[]> menuList = entityManager.createNativeQuery(query).getResultList();
        //List<Object[]> menuList = entityManager.createNativeQuery(query, Object[].class).getResultList();
        // 타입을 애초에 지정하면 오류가 난다. unknown entity
        //then
        assertNotNull(menuList);
        menuList.forEach(row -> {
            Stream.of(row).forEach(col -> System.out.print(col + " "));
            System.out.println();
        });
    }


    @Test
    public void 자동_결과_매핑을_사용한_조회_테스트(){
        //when
        String query = "SELECT"
                + " a.category_code, a.category_name, a.ref_category_code, NVL(v.menu_count, 0) menu_count"
                + " FROM tbl_category a"
                + " LEFT JOIN (SELECT COUNT(*) AS menu_count, b.category_code"
                + "            FROM tbl_menu b"
                + "            GROUP BY B.category_code) v ON (a.category_code = v.category_code)"
                + " ORDER BY 1";

        Query nativeQuery = entityManager.createNativeQuery(query, "categoryCountAutoMapping");
        // (query구문과 resultSetMapping명)
        List<Object[]> categoryList = nativeQuery.getResultList();
        // 전달해주는 결과가 엔터티와 컬럼이라 Object[]로 받음 ..?
        //then
        assertNotNull(categoryList);
        assertTrue(entityManager.contains(categoryList.get(0)[0]));
        categoryList.forEach(row -> {
            Stream.of(row).forEach(col -> System.out.print(col + " ")); // 정보를 옆으로 출력하기 위해서
            System.out.println();
        });
    }


    @Test
    public void 수동_결과_매핑을_사용한_조회_테스트(){
        //when
        String query = "SELECT"
                + " a.category_code, a.category_name, a.ref_category_code, NVL(v.menu_count, 0) menu_count"
                + " FROM tbl_category a"
                + " LEFT JOIN (SELECT COUNT(*) AS menu_count, b.category_code"
                + "            FROM tbl_menu b"
                + "            GROUP BY B.category_code) v ON (a.category_code = v.category_code)"
                + " ORDER BY 1";

        Query nativeQuery = entityManager.createNativeQuery(query, "categoryCountManualMapping");
        // (query구문과 resultSetMapping명)
        List<Object[]> categoryList = nativeQuery.getResultList();
        // 전달해주는 결과가 엔터티와 컬럼이라 Object[]로 받음 ..?
        //then
        assertTrue(entityManager.contains(categoryList.get(0)[0]));
        assertNotNull(categoryList);
        categoryList.forEach(row -> {
            Stream.of(row).forEach(col -> System.out.print(col + " ")); // 정보를 옆으로 출력하기 위해서
            System.out.println();
        });
    }
}
