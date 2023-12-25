package etu2074.framework.redirection;

public class Redirect {
    private RedirectAttribute redirectAttribute = new RedirectAttribute();

    public void setRedirectAttribute(RedirectAttribute redirectAttribute) {
        this.redirectAttribute = redirectAttribute;
    }

    public RedirectAttribute getRedirectAttribute() {
        return redirectAttribute;
    }

    public void addItem(String key, Object value) {
        redirectAttribute.addRedirectAttribute(key, value);
    }
}
