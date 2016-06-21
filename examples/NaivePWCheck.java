// MIT License
//
// Copyright (c) 2016 Valentin Wuestholz
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

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
