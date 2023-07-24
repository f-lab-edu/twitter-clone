package clone.twitter.service;

public interface SignInService {

    public void signInUser(String userID);

    public void signOutUser();

    public String getSignedInUserId();
}
