[HOME](../../../../README.md)
# resolve:dependency

Specify a dependency either as a full coordinate or using individual
group/artifact/version etc. See [resolve:resolve](../../../../org/oddjob/maven/jobs/ResolveJob.md) and [resolve:dependencies](../../../../org/oddjob/maven/types/DependenciesFactory.md)
for examples.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [artifactId](#propertyartifactid) | The artifact name. | 
| [classifier](#propertyclassifier) | The classifier of the artifact. | 
| [coords](#propertycoords) | The full Coordinates of the artifact. | 
| [exclusions](#propertyexclusions) | Exclusion from transitive dependencies. | 
| [groupId](#propertygroupid) | The group id of the artifact. | 
| [scope](#propertyscope) | The scope of the artifact. | 
| [systemPath](#propertysystempath) | Specify an absolute path of the artifact. | 
| [type](#propertytype) | The type of the artifact. | 
| [version](#propertyversion) | The version of the artifact. | 
| [versionlessKey](#propertyversionlesskey) |  | 


### Property Detail
#### artifactId <a name="propertyartifactid"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
</table>

The artifact name.

#### classifier <a name="propertyclassifier"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
</table>

The classifier of the artifact.

#### coords <a name="propertycoords"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>WRITE_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>Yes unless specified by individual attributes.</td></tr>
</table>

The full Coordinates of the artifact. The format is the same as Maven
of `<groupId>:<artifactId>:<version>[[:<type>[:<classifier>]]:<scope>].`

#### exclusions <a name="propertyexclusions"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
</table>

Exclusion from transitive dependencies.

#### groupId <a name="propertygroupid"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
</table>

The group id of the artifact.

#### scope <a name="propertyscope"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
</table>

The scope of the artifact.

#### systemPath <a name="propertysystempath"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
</table>

Specify an absolute path of the artifact.

#### type <a name="propertytype"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
</table>

The type of the artifact.

#### version <a name="propertyversion"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ATTRIBUTE</td></tr>
      <tr><td><i>Access</i></td><td>READ_WRITE</td></tr>
</table>

The version of the artifact.

#### versionlessKey <a name="propertyversionlesskey"></a>

<table style='font-size:smaller'>
      <tr><td><i>Access</i></td><td>READ_ONLY</td></tr>
</table>




-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
