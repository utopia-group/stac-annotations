# Library for STAC Analysis Annotations

[![Build Status][1]][2]

[1]: https://travis-ci.com/utopia-group/stac-annotations.svg?token=k4yWxxnayJzRvM74ZNks&branch=master
[2]: https://travis-ci.com/utopia-group/stac-annotations

## Introduction

Analyzing the challenge programs that were proposed in the first engagement currently
requires a substantial amount of manual effort. For instance, for each question it is
necessary to consult the description and question to determine which parts of the system
are secret and which parts are controlled by the user, including to what extent (e.g.,
bounds on the size of inputs).

This human component makes it hard to automate the analysis and even harder to compare
results between independent teams since the description may not be fully unambiguous and,
more importantly, understanding it may require some insights from inspecting the code. We
propose a set of lightweight annotations that all teams can use as inputs for their
tools. An additional benefit of our annotations is that software engineers will be able to
use them to detect vulnerabilities related to space/time resource usage, resulting in more
immediate effects on industry best-practices.


## Annotations

Our annotations cover all four crucial parameters for analyzing challenge programs: 1)
identification of secret inputs, 2) identification of user-inputs, 3) restriction of
inputs based on bounds in the questions, and 4) identification of entry methods for the
analysis. None of them affects the run-time behavior of the program.

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

   By identifying an entry method for the analysis, a user will be able to select a
   "scope" (i.e., start and end) for the analysis. This will allow the analysis to only
   focus on the entry method and its callees. To annotate such methods we propose to
   annotate the method with a custom `@Check` Java annotation. This annotation also
   takes arguments for the type of analysis (confidentiality or vulnerability) and the
   type of resource (space or time).

### Example

   Method `verifyPassword` in the class below demonstrates how these annotations can be
   used in practice. In particular, an analysis is able to rely on the following:

   - The object `this.password` is a secret input (see line 11).
   - The parameter `s` is a user input (see line 10).
   - The size of the input `s` is less than 64 (see line 12).
   - The analysis can start at the beginning of method `verifyPassword` and can stop at
     the end of that method (see line 8). The analysis should check for confidentiality
     vulnerabilities with respect to time.

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
        STAC.makeUserInput(s);
        STAC.makeSecretInput(this.password);
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
