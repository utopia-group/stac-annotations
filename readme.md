# Library for STAC Analysis Annotations

[![Build Status][1]][2]

[1]: https://travis-ci.org/utopia-group/stac-annotations.svg?branch=master
[2]: https://travis-ci.org/utopia-group/stac-annotations

This library provides a set of lightweight annotations for specifying program properties
concerning space/time usage (see [DARPA STAC
program](http://www.darpa.mil/program/space-time-analysis-for-cybersecurity)). These
properties can be checked by automatic space/time analyses.

## Annotations

The library provides annotations for all four space/time analysis parameters: 1)
identification of secret inputs, 2) identification of user-inputs, 3) restriction of
inputs, and 4) identification of entry methods for the analysis. None of them affects the
run-time behavior of the program.

### Identification of secret inputs

   To identify secret inputs, we propose to use calls to a designated method
   `STAC.makeSecretInput` with an empty body that takes an object as input. An analysis
   will only treat the arguments of calls to this method as secret inputs.

### Identification of user inputs

   Like for secret inputs, we propose to use a separate designated method
   `STAC.makeUserInput` to identify user inputs. An analysis will only treat the arguments
   of calls to this method as user inputs.

### Restriction of inputs

   To restrict the size of a given input we propose calls to a designated method
   `STAC.assume` with an empty body that takes a boolean condition as input (e.g., `x <
   10000`). An analysis will be able to assume the input condition to hold.

### Identification of entry methods and analysis type

   By identifying an entry method for the analysis, a user is able to select a
   "scope" (i.e., start and end) for the analysis. This will allow the analysis to only
   focus on the entry method and its callees. To annotate such methods we propose to
   annotate the method with a custom `@Check` Java annotation. This annotation also takes
   arguments for the type of analysis (confidentiality or vulnerability) and the type of
   resource (space or time). We support multiple such annotations via a custom `@Checks`
   Java annotation.

### Example

   Method `verifyPassword` in the class below demonstrates how these annotations can be
   used in practice.

``` Java
import edu.utexas.stac.Check;
import edu.utexas.stac.STAC;

public class NaivePWCheck {
    private String password;

    public NaivePWCheck(final String password) {
        this.password = password;
    }

    @Check(analysis = Check.Analysis.CONFIDENTIALITY, resource = Check.Resource.TIME)
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
```

   In particular, an analysis is able to make use of the following:

   - `STAC.makeSecretInput(this.password)` indicates that the object `this.password` is a secret input.
   - `STAC.makeUserInput(s)` indicates that the parameter `s` is a user input.
   - `STAC.assume(s.length() < 64)` indicates that size of the input `s` is less than 64.
   - `@Check(analysis = Check.Analysis.CONFIDENTIALITY, resource = Check.Resource.TIME)`
     indicates that an analysis can start at the beginning of method `verifyPassword` and
     can stop at the end of that method. It also indicates that the user is interested in
     checking for confidentiality vulnerabilities with respect to time.
