Siddhi IO WSO2Event
======================================

[![Jenkins Build Status](https://wso2.org/jenkins/job/siddhi/job/siddhi-io-wso2event/badge/icon)](https://wso2.org/jenkins/job/siddhi/job/siddhi-io-wso2event/)
[![GitHub Release](https://img.shields.io/github/release/siddhi-io/siddhi-io-wso2event.svg)](https://github.com/siddhi-io/siddhi-io-wso2event/releases)
[![GitHub Release Date](https://img.shields.io/github/release-date/siddhi-io/siddhi-io-wso2event.svg)](https://github.com/siddhi-io/siddhi-io-wso2event/releases)
[![GitHub Open Issues](https://img.shields.io/github/issues-raw/siddhi-io/siddhi-io-wso2event.svg)](https://github.com/siddhi-io/siddhi-io-wso2event/issues)
[![GitHub Last Commit](https://img.shields.io/github/last-commit/siddhi-io/siddhi-io-wso2event.svg)](https://github.com/siddhi-io/siddhi-io-wso2event/commits/master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

The **siddhi-io-wso2event extension** is an extension to <a target="_blank" href="https://wso2.github.io/siddhi">Siddhi</a> that receives and publishes events in the WSO2Event format via Thrift or Binary protocols.

Find some useful links below:

* <a target="_blank" href="https://github.com/wso2-extensions/siddhi-io-wso2event">Source code</a>
* <a target="_blank" href="https://github.com/wso2-extensions/siddhi-io-wso2event/releases">Releases</a>
* <a target="_blank" href="https://github.com/wso2-extensions/siddhi-io-wso2event/issues">Issue tracker</a>

## Downloads
* Versions 3.x and above with group id `io.siddhi.extension.*` from <a target="_blank" href="https://mvnrepository.com/artifact/io.siddhi.extension.io.wso2event/siddhi-io-wso2event/">here</a>.
* Versions 2.x and lower with group id `org.wso2.extension.siddhi.` from  <a target="_blank" href="https://mvnrepository.com/artifact/io.siddhi.extension.io.wso2event/siddhi-io-wso2event">here</a>.

## Latest API Docs 

Latest API Docs is <a target="_blank" href="https://wso2-extensions.github.io/siddhi-io-wso2event/api/5.0.0">5.0.0</a>.

## Features

* <a target="_blank" href="https://wso2-extensions.github.io/siddhi-io-wso2event/api/5.0.0/#wso2event-sink">wso2event</a> *(<a target="_blank" href="http://siddhi.io/en/v5.1/docs/query-guide/#sink">Sink</a>)*<br> <div style="padding-left: 1em;"><p><p style="word-wrap: break-word;margin: 0;">The WSO2Event source pushes wso2events via TCP (databridge) in <code>wso2event</code> format. You can send wso2events through <code>Thrift</code> or <code>Binary</code> protocols.</p></p></div>
* <a target="_blank" href="https://wso2-extensions.github.io/siddhi-io-wso2event/api/5.0.0/#wso2event-source">wso2event</a> *(<a target="_blank" href="http://siddhi.io/en/v5.1/docs/query-guide/#source">Source</a>)*<br> <div style="padding-left: 1em;"><p><p style="word-wrap: break-word;margin: 0;">The WSO2Event source receives wso2events via TCP (databridge) in <code>wso2event</code> format. You can receive wso2events through <code>Thrift</code> or <code>Binary</code> protocols.</p></p></div>

## Dependencies

There are no other dependencies needed for this extension.

## Installation

For installing this extension on various siddhi execution environments refer Siddhi documentation section on <a target="_blank" href="https://siddhi.io/redirect/add-extensions.html">adding extensions</a>.

# Support and Contribution

* We encourage users to ask questions and get support via <a target="_blank" href="https://stackoverflow.com/questions/tagged/siddhi">StackOverflow</a>, make sure to add the `siddhi` tag to the issue for better response.
* If you find any issues related to the extension please report them on <a target="_blank" href="https://github.com/siddhi-io/siddhi-execution-string/issues">the issue tracker</a>.
* For production support and other contribution related information refer <a target="_blank" href="https://siddhi.io/community/">Siddhi Community</a> documentation.
