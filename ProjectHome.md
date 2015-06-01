Monitor [Hudson](https://hudson.dev.java.net/) build status from [Eclipse](http://eclipse.org).

Install using the Update Manager or drop dk.contix.eclipse.hudson\_x.x.x.jar in the plugins/ dir.

The update site url is http://hudson-eclipse.googlecode.com/svn/trunk/hudson-update/


After installation, the plugin must be configured. This is done in Preferences under Hudson. The important parameter is Hudson base url, which should point to the main Hudson page.

When the plugin is running, a health icon is displayed at the bottom of the Eclipse window. The icon is red on build failure and green on success. Double-click on the icon to open the Hudson view, where all projects in Hudson can be tracked. Please note that due to limitations in Eclipse, the Hudson view must be active before the icon is displayed.

## Changelog ##

**1.0.10 (2009-11-05)**
  * [Issue #58](https://code.google.com/p/hudson-eclipse/issues/detail?id=#58): Invalid thread access
  * [Issue #59](https://code.google.com/p/hudson-eclipse/issues/detail?id=#59): Update job not scheduled


**1.0.9 (2009-10-15)**
  * [Issue #49](https://code.google.com/p/hudson-eclipse/issues/detail?id=#49): Only support JDK6
  * [Issue #51](https://code.google.com/p/hudson-eclipse/issues/detail?id=#51): Invalid thread access when refreshing status
  * From now on, only JDK6 is supported as runtime environment


**1.0.8 (2009-10-01)**
  * [Issue #36](https://code.google.com/p/hudson-eclipse/issues/detail?id=#36): Improved support for parameterized builds
  * [Issue #39](https://code.google.com/p/hudson-eclipse/issues/detail?id=#39): Adjust loglevel
  * [Issue #40](https://code.google.com/p/hudson-eclipse/issues/detail?id=#40): Support HTTP Basic Authentication
  * [Issue #42](https://code.google.com/p/hudson-eclipse/issues/detail?id=#42): Fix bugs in Discovery mechanism
  * [Issue #45](https://code.google.com/p/hudson-eclipse/issues/detail?id=#45): Add date/time of last build to job listing
  * [Issue #47](https://code.google.com/p/hudson-eclipse/issues/detail?id=#47): Do not block UI when refreshing


**1.0.7 (2009-05-20)**
> Fixed regressions in 1.0.6
  * [Issue 33](https://code.google.com/p/hudson-eclipse/issues/detail?id=33): NPE when getting job status due to missing support for disabled builds
  * [Issue 34](https://code.google.com/p/hudson-eclipse/issues/detail?id=34): Handle multiple health statuses in a job
  * [Issue 35](https://code.google.com/p/hudson-eclipse/issues/detail?id=35): Handle disabled build status, and don't break when unknown status is encountered
  * [Issue 36](https://code.google.com/p/hudson-eclipse/issues/detail?id=36): Persist used parameters when scheduling parameterized build


**1.0.6 (2009-05-17)**
  * [Issue 30](https://code.google.com/p/hudson-eclipse/issues/detail?id=30): Discover Hudson instances by broadcasting
  * [Issue 31](https://code.google.com/p/hudson-eclipse/issues/detail?id=31): Support parameterized builds by prompting for parameters when scheduling build
  * [Issue 32](https://code.google.com/p/hudson-eclipse/issues/detail?id=32): Show build health in addition to status


**1.0.5 (2009-04-07)**
  * Decreased connection timeout - prevents hanging workbench
  * Support for form-based authentication
  * Fix [issue #27](https://code.google.com/p/hudson-eclipse/issues/detail?id=#27): Base url must end with a /
  * Fix [issue #23](https://code.google.com/p/hudson-eclipse/issues/detail?id=#23): Restore view state
  * Fix [issue #22](https://code.google.com/p/hudson-eclipse/issues/detail?id=#22): Missing log4j
  * Fix [issue #19](https://code.google.com/p/hudson-eclipse/issues/detail?id=#19): Browser problems in Eclipse 3.4
  * Fix [issue #18](https://code.google.com/p/hudson-eclipse/issues/detail?id=#18): Only reload jobs when necessary
  * Fix [issue #16](https://code.google.com/p/hudson-eclipse/issues/detail?id=#16): Remove status icon when view is closed
  * Fix [issue #14](https://code.google.com/p/hudson-eclipse/issues/detail?id=#14): Whitespaces in job names
  * Fix [issue #9](https://code.google.com/p/hudson-eclipse/issues/detail?id=#9): Connection problems


**1.0.4 (2008-03-04)**
  * Added name filter - Thanks to Christian Fain for the patch
  * Added support for Hudson views. Select view from the dropdown menu.
  * Make error popup behavior configurable. If the connection to Hudson disappears, the status icon changes, but no popup appears unless configured in the preferences.
  * Fixed URL handling bug where two slashes were inserted in the URL
  * Fixed bug where an internal exception was thrown if the error popup was not closed before the next update.


**1.0.3 (2007-10-16)**
  * Support for SSL connections with self-signed certificates
  * Support for security tokens per project
  * Use ImageRegistry to retrieve icons
  * Read proxy settings from Eclipse

  * This version requires Eclipse 3.3
