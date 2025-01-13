//package com.example.dailyhub.data;
//
//import com.example.dailyhub.data.repository.PostRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.time.LocalDate;
//import java.util.stream.LongStream;
//
//@Slf4j
//@Configuration
//@Profile({"test"})
//public class NotProd {
//
//  @Bean
//  CommandLineRunner initData(
//      PostRepository postRepository,
//      PasswordEncoder passwordEncoder
//  ) {
//    return (args) -> {
//      log.info("init data...");
//
//      LongStream.rangeClosed(1, 10).forEach(i -> {
//        // todo data
//        Todo todo = Todo.builder()
//            .title("Title..." + i)
//            .writer("nick_" + i)
//            .complete(i % 2 == 0)
//            .dueDate(LocalDate.now().plusDays(1))
//            .build();
//        todoRepository.save(todo);
//        // product data
//        // 1. 상품 10개 추가
//        Product product = Product.builder()
//            .pname("상품" + i)
//            .price((int) (100 * i))
//            .pdesc("상품설명 " + i)
//            .stockNumber(100)
//            .build();
//
//        // 2개의 이미지 파일 추가
//        product.addImageString("IMAGE1.jpg");
//        product.addImageString("IMAGE2.jpg");
//
//        productRepository.save(product);
//
//
//      });
//    };
//  }
//
//}
