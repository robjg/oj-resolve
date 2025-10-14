[HOME](../../../../README.md)
# resolve:resolve

Resolves dependencies to a list of files downloaded to the local repo.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [dependencies](#propertydependencies) | The dependencies to resolve. | 
| [name](#propertyname) | The name of this job. | 
| [noDefaultRepos](#propertynodefaultrepos) | Use default repos (true/false). | 
| [noSettingsRepos](#propertynosettingsrepos) | Use repos from the settings (true/false). | 
| [remoteRepositories](#propertyremoterepositories) | Optional repositories to use. | 
| [resolvedFiles](#propertyresolvedfiles) | A List of resolved files. | 
| [resolvedFilesArray](#propertyresolvedfilesarray) | An array of resolved files. | 
| [resolvedPaths](#propertyresolvedpaths) | A List of resolved paths. | 
| [resolvedPathsArray](#propertyresolvedpathsarray) | An array of resolved paths. | 
| [resolverSession](#propertyresolversession) | The Session to use. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Simple resolve. |


### Property Detail
#### dependencies <a name="propertydependencies"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>Yes.</td></tr>
</table>

The dependencies to resolve. See `org.oddjob.maven.types.DependencyContainer`.

#### name <a name="propertyname"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

The name of this job.

#### noDefaultRepos <a name="propertynodefaultrepos"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. The default repos will be used.</td></tr>
</table>

Use default repos (true/false).

#### noSettingsRepos <a name="propertynosettingsrepos"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No. Settings repos will be used.</td></tr>
</table>

Use repos from the settings (true/false).

#### remoteRepositories <a name="propertyremoterepositories"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
      <tr><td><i>Required</i></td><td>No, Defaults will be used.</td></tr>
</table>

Optional repositories to use.

#### resolvedFiles <a name="propertyresolvedfiles"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>R/O.</td></tr>
</table>

A List of resolved files.

#### resolvedFilesArray <a name="propertyresolvedfilesarray"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>R/O.</td></tr>
</table>

An array of resolved files. A convenience to make this
easier to use with an ` org.oddjob.util.URLClassLoaderType`.

#### resolvedPaths <a name="propertyresolvedpaths"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>R/O.</td></tr>
</table>

A List of resolved paths.

#### resolvedPathsArray <a name="propertyresolvedpathsarray"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>R/O.</td></tr>
</table>

An array of resolved paths. A convenience to make this
easier to use.

#### resolverSession <a name="propertyresolversession"></a>

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
