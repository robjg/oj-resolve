[HOME](../../../../README.md)
# resolve:dependencies

Provide a list of dependencies. This may be nested.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [dependencies](#propertydependencies) | List of dependencies. | 
| [file](#propertyfile) | A text file of dependencies. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Resolve a list of dependencies. |


### Property Detail
#### dependencies <a name="propertydependencies"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>WRITE_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

List of dependencies.

#### file <a name="propertyfile"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

A text file of dependencies.


### Examples
#### Example 1 <a name="example1"></a>

Resolve a list of dependencies.

```xml
<oddjob id="oddjob">
    <job>
        <resolve:resolve id="resolve" xmlns:resolve="oddjob:resolve">
            <dependencies>
                <resolve:dependencies>
                    <dependencies>
                        <resolve:dependency coords="commons-logging:commons-logging:1.2"/>
                        <resolve:dependency coords="commons-collections:commons-collections:3.2.2"/>
                    </dependencies>
                </resolve:dependencies>
            </dependencies>
        </resolve:resolve>
    </job>
</oddjob>
```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
