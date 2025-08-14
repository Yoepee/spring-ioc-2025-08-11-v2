/**
 * Reflection 학습용 코드
 */
package com.ll;

import org.reflections.Reflections;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

public class ReflectionExample {

    public static void main(String[] args) throws Exception {

        // ==========================================
        // Test1 : 클래스 로딩
        // ==========================================
        Class<?> clazz = Class.forName("java.util.ArrayList");
        System.out.println("========== Test1 : 클래스 로딩 ==========");
        System.out.println("기능       : 클래스 로딩");
        System.out.println("출력 형식  : 클래스 이름");
        System.out.println("클래스 이름 : " + clazz.getName());
        System.out.println();

        // ==========================================
        // Test2-1 : 기본 생성자 호출
        // ==========================================
        class User1 {
            public User1() {
                System.out.println("User1 객체 생성됨");
            }
        }

        Constructor<User1> constructor1 = User1.class.getDeclaredConstructor();
        System.out.println("========== Test2-1 : 기본 생성자 호출 ==========");
        System.out.println("기능       : 객체 생성 (기본 생성자)");
        System.out.println("출력 형식  : 객체 생성 메시지");
        User1 user1 = constructor1.newInstance();
        System.out.println();

        // ==========================================
        // Test2-2 : 파라미터 있는 생성자 호출
        // ==========================================
        class User2 {
            private String name;
            public User2(String name) {
                this.name = name;
            }
            public void sayHello() {
                System.out.println("Hello " + name);
            }
        }

        System.out.println("========== Test2-2 : 파라미터 있는 생성자 호출 ==========");
        System.out.println("기능       : 객체 생성 (파라미터 있는 생성자)");
        System.out.println("출력 형식  : 메서드 실행 결과");
        User2 user2 = User2.class.getDeclaredConstructor(String.class).newInstance("Alice");
        user2.sayHello();
        System.out.println();

        // ==========================================
        // Test3-1 : public 메서드 호출
        // ==========================================
        class User3 {
            public void greet(String name) {
                System.out.println("Hi " + name);
            }
        }

        System.out.println("========== Test3-1 : public 메서드 호출 ==========");
        System.out.println("기능       : public 메서드 실행");
        System.out.println("출력 형식  : 메서드 실행 결과");
        Method method1 = User3.class.getMethod("greet", String.class);
        User3 user3 = new User3();
        method1.invoke(user3, "Bob");
        System.out.println();

        // ==========================================
        // Test3-2 : private 메서드 호출
        // ==========================================
        class User4 {
            private void secret() {
                System.out.println("Secret");
            }
        }

        System.out.println("========== Test3-2 : private 메서드 호출 ==========");
        System.out.println("기능       : private 메서드 실행");
        System.out.println("출력 형식  : 메서드 실행 결과");
        Method method2 = User4.class.getDeclaredMethod("secret");
        method2.setAccessible(true);
        method2.invoke(new User4());
        System.out.println();

        // ==========================================
        // Test4 : 필드 접근
        // ==========================================
        class User5 {
            private String name;
        }

        System.out.println("========== Test4 : 필드 접근 ==========");
        System.out.println("기능       : private 필드 값 설정 및 조회");
        System.out.println("출력 형식  : 필드 값");
        User5 user5 = new User5();
        Field field = User5.class.getDeclaredField("name");
        field.setAccessible(true);
        field.set(user5, "Charlie");
        System.out.println("name 필드 값 : " + field.get(user5));
        System.out.println();

        // ==========================================
        // Test5 : 애노테이션 읽기
        // ==========================================
        @MyAnnotation("예?") //속성이 한 개라면 속성 이름 생략 가능, 그러나 속성이 여러 개 라면 value="예?"
        class TestClass { }

        System.out.println("========== Test5 : 애노테이션 읽기 ==========");
        System.out.println("기능       : 클래스에 붙은 애노테이션 조회");
        System.out.println("출력 형식  : 애노테이션 값");

        //TestClass.class는 TestClass 인스턴스가 아님, 클래스 정보(메타 데이터)를 담고 있는 객체임, 당연히 값을 집어넣거나 할 수 없음
        MyAnnotation ann = TestClass.class.getAnnotation(MyAnnotation.class); //MyAnnotation이 안 붙어있으면 null 반환
        System.out.println("애노테이션 값 : " + ann.value());
        System.out.println();

        // ==========================================
        // Test6 : 특정 어노테이션이 붙은 클래스 조회
        // ==========================================
        System.out.println("========== Test6 : 특정 어노테이션이 붙은 클래스 조회 ==========");
        System.out.println("기능       : 어노테이션이 붙은 클래스 검색");
        System.out.println("출력 형식  : 클래스 이름 목록");
        Reflections reflections = new Reflections("com.ll");
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(MyAnnotation.class);
        for (Class<?> c : annotatedClasses) {
            System.out.println("클래스 이름 : " + c.getName());
        }
        System.out.println();
    }
}

@Retention(RetentionPolicy.RUNTIME) //어노테이션의 유효 시간 지정, 현재는 런타임까지 유지
@interface MyAnnotation { //@interface로 어노테이션 정의, 어노테이션 전용 타입임을 나타냄
    String value(); //어노테이션 속성
}

// ==========================================
// MyAnnotation이 붙은 클래스 예제
// ==========================================
@MyAnnotation("첫 번째 클래스")
class TestClass1 { }

@MyAnnotation("두 번째 클래스")
class TestClass2 { }

@MyAnnotation("세 번째 클래스")
class TestClass3 { }

// 다른 클래스는 어노테이션 없음
class NoAnnotationClass { }