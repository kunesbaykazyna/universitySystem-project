package models;

public abstract class UserDecorator implements UserComponent {
    protected UserComponent decoratedUser;

    public UserDecorator(UserComponent user) {
        this.decoratedUser = user;
    }

    @Override
    public String getName() {
        return decoratedUser.getName();
    }
}