
package com.seoulotakus.takumapbe.domain.category.config;

import com.seoulotakus.takumapbe.domain.category.entity.Category;
import com.seoulotakus.takumapbe.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 애플리케이션 시작 시 카테고리 초기 데이터를 자동으로 삽입하는 컴포넌트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryDataLoader implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        // 이미 카테고리 데이터가 있으면 스킵
        if (categoryRepository.count() > 0) {
            log.info("카테고리 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("카테고리 초기 데이터를 삽입합니다...");

        List<String> categoryNames = Arrays.asList(
                "가챠샵",
                "굿즈샵",
                "피규어샵",
                "애니메이션 카페",
                "코스프레 의상점",
                "만화책방",
                "성지순례지"
        );

        for (String name : categoryNames) {
            Category category = Category.builder()
                    .name(name)
                    .build();
            categoryRepository.save(category);
            log.info("카테고리 추가: {}", name);
        }

        log.info("카테고리 초기 데이터 삽입 완료! 총 {}개의 카테고리가 추가되었습니다.", categoryNames.size());
    }
}
