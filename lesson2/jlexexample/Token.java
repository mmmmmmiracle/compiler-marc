
public class Token {

  private String token;
  private int value;
  private String name;

  public Token (String token) {
    this.token = token;
  }

  public Token (String token, int value) {
    this.token=token;
    this.value=value;
  }

  public Token (String token, String name) {
    this.token=token;
    this.name=name;
  }

  public String getToken() {
    return token;
  }

  public int getValue() {
    return value;
  }

  public String getName() {
    return name;
  }
}
 
