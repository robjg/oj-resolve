[HOME](../../../../README.md)
# resolve:resolve

Resolves dependencies to a list of files downloaded to the local repo.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [arooaSession](#propertyarooaSession) |  | 
| [dependencies](#propertydependencies) | The dependencies to resolve. | 
| [name](#propertyname) | The name of this job. | 
| [noDefaultRepos](#propertynoDefaultRepos) | Use default repos (true/false). | 
| [noSettingsRepos](#propertynoSettingsRepos) | Use repos from the settings (true/false). | 
| [remoteRepositories](#propertyremoteRepositories) | Optional repositories to use. | 
| [resolvedFiles](#propertyresolvedFiles) | A List of resolved files. | 
| [resolvedFilesArray](#propertyresolvedFilesArray) | An array of resolved files. | 
| [resolverSession](#propertyresolverSession) | The Session to use. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Simple resolve. |


### Property Detail
#### arooaSession <a name="propertyarooaSession"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>WRITE_ONLY</td></tr>
</table>



#### dependencies <a name="propertydependencies"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Yes.</td></tr>
</table>

The dependencies to resolve. See [org.oddjob.maven.types.DependencyContainer](http://rgordon.co.uk/oddjob/1.6.0/api/org/oddjob/maven/types/DependencyContainer.html).

#### name <a name="propertyname"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The name of this job.

#### noDefaultRepos <a name="propertynoDefaultRepos"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. The default repos will be used.</td></tr>
</table>

Use default repos (true/false).

#### noSettingsRepos <a name="propertynoSettingsRepos"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. Settings repos will be used.</td></tr>
</table>

Use repos from the settings (true/false).

#### remoteRepositories <a name="propertyremoteRepositories"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, Defaults will be used.</td></tr>
</table>

Optional repositories to use.

#### resolvedFiles <a name="propertyresolvedFiles"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>R/O.</td></tr>
</table>

A List of resolved files.

#### resolvedFilesArray <a name="propertyresolvedFilesArray"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>R/O.</td></tr>
</table>

An array of resolved files. A convenience to make this
easier to use with an <code>org.oddjob.util.URLClassLoaderType </code>.

#### resolverSession <a name="propertyresolverSession"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, All defaults will be used.</td></tr>
</table>

The Session to use. See [resolve:session](../../../../org/oddjob/maven/types/ResolverSessionType.md).


### Examples
#### Example 1 <a name="example1"></a>

Simple resolve.

```xml
<oddjob id="oddjob">
    <job>
        <resolve:resolve id="resolve" xmlns:resolve="oddjob:resolve">
            <dependencies>
                <resolve:dependency coords="commons-beanutils:commons-beanutils:1.9.4"/>
            </dependencies>
        </resolve:resolve>
    </job>
</oddjob>
```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
