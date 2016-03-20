# pitest-junit-5-plugin
A pitest plugin that integrates JUnit 5 test with pitest mutation test engine.


## Compatiblity Note
This plugin provides JUnit 5 integration.
JUnit 5 requires Java 8 minimum. 
Thus **this plugin requires Java 8** as minimum Java version as well, although pitest runs with Java 6 and newer.


## WARNING
The pitest-junit-5-plugin was written against JUnit 5.0.0.ALPHA.
The plugin itself as well as JUnit 5 are in an early ALPHA state.
API might change at any time and the pitest-junit-5-plugin might not be compatible with the latest JUnit 5 release.
I do not recommend using the plugin for production tests.




## Future
The JUnit 5 plugin uses an extension point of the pitest mutation engine to enable JUnit 5 tests to be mutation tested with pitest.
This plugin will be maintained separately from pitest and might get incorporated some day.
The license of the JUnit 5 plugin (Apache 2.0) is compatible with the pitest license and the author is willing to incorporate this functionality into pitest core.
Neveretheless this plugin needs Java 8 (this is a requirement from JUnit 5) and pitest maintains compatibility with Java 6.
Thus it might not be possible to merge the plugin into core.
