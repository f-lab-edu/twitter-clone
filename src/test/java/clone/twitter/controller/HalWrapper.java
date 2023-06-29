package clone.twitter.controller;

import java.util.List;
import java.util.Map;
import org.springframework.hateoas.Link;

public class HalWrapper<T> {
    private Embedded<T> _embedded;

    private Map<String, Link> _links;

    public Embedded<T> get_embedded() {
        return _embedded;
    }

    public void set_embedded(Embedded<T> _embedded) {
        this._embedded = _embedded;
    }

    public Map<String, Link> get_links() {
        return _links;
    }

    public void set_links(Map<String, Link> _links) {
        this._links = _links;
    }

    public static class Embedded<T> {
        private List<T> userFollowResponseDtoList;

        public List<T> getUserFollowResponseDtoList() {
            return userFollowResponseDtoList;
        }

        public void setUserFollowResponseDtoList(List<T> userFollowResponseDtoList) {
            this.userFollowResponseDtoList = userFollowResponseDtoList;
        }
    }
}
