package clone.twitter.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

// 네이밍은 ProfileModel이 더 적절(사실 profile이란 용어는 링크의 관계상태profile과 겹치므로 용어 구분 정의 필요). 이후 수정. 진짜 의미의 index 페이지는 로그인/로그아웃 상태와 상관없이 timeline 페이지(현재는 로그인 상태의 타임라인 페이지만 존재)
public class IndexModel extends EntityModel<Void> {
    public IndexModel() {
        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));

        add(Link.of("/docs/index.html#delete-tweet").withRel("profile"));
    }
}
