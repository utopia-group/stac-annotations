import edu.utexas.stac.Check;
import edu.utexas.stac.Checks;
import edu.utexas.stac.STAC;

public class NaivePWCheck {
    private String password;

    public NaivePWCheck(final String password) {
        this.password = password;
    }


    @Checks({@Check(analysis = Check.Analysis.CONFIDENTIALITY, resource = Check.Resource.TIME), @Check(analysis = Check.Analysis.CONFIDENTIALITY, resource = Check.Resource.SPACE)})
    public boolean verifyPassword(final String s) {
        STAC.makeSecretInput(this.password);
        STAC.makeUserInput(s);
        STAC.assume(s.length() < 64);
        for (int i = 0; i < s.length(); ++i) {
            if (i >= this.password.length() ||
                    s.charAt(i) != this.password.charAt(i)) {
                return false;
            }
            try {
                Thread.sleep(25L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return s.length() >= this.password.length();
    }
}
