# API Docs - v5.0.2

!!! Info "Tested Siddhi Core version: *<a target="_blank" href="http://siddhi.io/en/v5.1/docs/query-guide/">5.1.24</a>*"
    It could also support other Siddhi Core minor versions.

## Sink

### wso2event *<a target="_blank" href="http://siddhi.io/en/v5.1/docs/query-guide/#sink">(Sink)</a>*
<p></p>
<p style="word-wrap: break-word;margin: 0;">The WSO2Event source pushes wso2events via TCP (databridge) in <code>wso2event</code> format. You can send wso2events through <code>Thrift</code> or <code>Binary</code> protocols.</p>
<p></p>
<span id="syntax" class="md-typeset" style="display: block; font-weight: bold;">Syntax</span>

```
@sink(type="wso2event", wso2.stream.id="<STRING>", url="<STRING>", auth.url="<STRING>", username="<STRING>", password="<STRING>", protocol="<STRING>", mode="<STRING>", @map(...)))
```

<span id="query-parameters" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">QUERY PARAMETERS</span>
<table>
    <tr>
        <th>Name</th>
        <th style="min-width: 20em">Description</th>
        <th>Default Value</th>
        <th>Possible Data Types</th>
        <th>Optional</th>
        <th>Dynamic</th>
    </tr>
    <tr>
        <td style="vertical-align: top">wso2.stream.id</td>
        <td style="vertical-align: top; word-wrap: break-word"><p style="word-wrap: break-word;margin: 0;">Stream Id to use when publishing events. If stream id is not defined, it uses the respective siddhi stream name with version 1.0.0.</p></td>
        <td style="vertical-align: top">`the defined stream id:1.0.0`. 
(e.g.,if the stream definition is `org.wso2.stream.bar.stream`, then the value is `org.wso2.stream.bar.stream:1.0.0`.)</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">url</td>
        <td style="vertical-align: top; word-wrap: break-word"><p style="word-wrap: break-word;margin: 0;">The URL to which the outgoing events published via TCP over Thrift or Binary. (e.g., <code>tcp://localhost:7611</code>)</p></td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">auth.url</td>
        <td style="vertical-align: top; word-wrap: break-word"><p style="word-wrap: break-word;margin: 0;">The Thrift/Binary server endpoint url which used for authentication purposes.</p></td>
        <td style="vertical-align: top">`ssl://localhost:<tcp-port> + 100` 
 (e.g., if the tcp port is 7611, then the value is `ssl://localhost:7711`)</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">username</td>
        <td style="vertical-align: top; word-wrap: break-word"><p style="word-wrap: break-word;margin: 0;">The username is used for authentication flow before publishing events. e.g., <code>admin</code></p></td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">password</td>
        <td style="vertical-align: top; word-wrap: break-word"><p style="word-wrap: break-word;margin: 0;">The password is used for authentication flow before publishing events. e.g., <code>admin</code></p></td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">protocol</td>
        <td style="vertical-align: top; word-wrap: break-word"><p style="word-wrap: break-word;margin: 0;">There are two protocols that we can use to publish events through data bridge. Either, we can use thrift or binary.</p></td>
        <td style="vertical-align: top">thrift</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">mode</td>
        <td style="vertical-align: top; word-wrap: break-word"><p style="word-wrap: break-word;margin: 0;">Property which decides whether to publish events in synchronous or asynchronous mode. It can be either <code>blocking</code> or <code>non-blocking</code> mode.</p></td>
        <td style="vertical-align: top">non-blocking</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
</table>

<span id="examples" class="md-typeset" style="display: block; font-weight: bold;">Examples</span>
<span id="example-1" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 1</span>
```
@sink(type='wso2event', wso2.stream.id='fooStream:1.0.0', url="tcp://localhost:7611", auth.url="ssl://localhost:7711", protocol="thrift", username="admin", password="admin", mode="non-blocking" , @map(type='wso2event'))
Define stream barStream(system string, price float, volume long);
```
<p></p>
<p style="word-wrap: break-word;margin: 0;">As defined in above query events are pushed to destination that defined.</p>
<p></p>
## Source

### wso2event *<a target="_blank" href="http://siddhi.io/en/v5.1/docs/query-guide/#source">(Source)</a>*
<p></p>
<p style="word-wrap: break-word;margin: 0;">The WSO2Event source receives wso2events via TCP (databridge) in <code>wso2event</code> format. You can receive wso2events through <code>Thrift</code> or <code>Binary</code> protocols.</p>
<p></p>
<span id="syntax" class="md-typeset" style="display: block; font-weight: bold;">Syntax</span>

```
@source(type="wso2event", wso2.stream.id="<STRING>", @map(...)))
```

<span id="query-parameters" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">QUERY PARAMETERS</span>
<table>
    <tr>
        <th>Name</th>
        <th style="min-width: 20em">Description</th>
        <th>Default Value</th>
        <th>Possible Data Types</th>
        <th>Optional</th>
        <th>Dynamic</th>
    </tr>
    <tr>
        <td style="vertical-align: top">wso2.stream.id</td>
        <td style="vertical-align: top; word-wrap: break-word"><p style="word-wrap: break-word;margin: 0;">Stream Id to consume events. If stream id is not defined, it uses the respective siddhi stream definition with version 1.0.0 <br></p></td>
        <td style="vertical-align: top">`the defined stream id:1.0.0`. 
(e.g. if the stream definition is `org.wso2.stream.bar.stream`, then the value is `org.wso2.stream.bar.stream:1.0.0`.)</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
</table>

<span id="examples" class="md-typeset" style="display: block; font-weight: bold;">Examples</span>
<span id="example-1" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 1</span>
```
@source(type='wso2event', wso2.stream.id='inputstream:1.0.0', @map(type='wso2event')) 
Define stream Foo (symbol string, price float, volume long);
```
<p></p>
<p style="word-wrap: break-word;margin: 0;">As defined in above query events are received to stream id that defined in source.</p>
<p></p>
