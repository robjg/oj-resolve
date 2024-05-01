[HOME](../../../../README.md)
# resolve:session

Provide a Session for resolving artifact from Maven. Allows settings
and other session properties to be overridden.

### Property Summary

| Property | Description |
| -------- | ----------- |
| [authentications](#propertyauthentications) | Specify authentications to be used in the session. | 
| [globalSettings](#propertyglobalSettings) | Specify a global settings file to be used in the session. | 
| [localRepository](#propertylocalRepository) | Specify a local repository to be used in the session. | 
| [mirrors](#propertymirrors) | Specify mirrors to be used in the session. | 
| [proxies](#propertyproxies) | Specify proxies to be used in the session. | 
| [userProperties](#propertyuserProperties) | Specify additional user properties to be set. | 
| [userSettings](#propertyuserSettings) | Specify a user settings file to be used in the session. | 


### Example Summary

| Title | Description |
| ----- | ----------- |
| [Example 1](#example1) | Specify a repository. |
| [Example 2](#example2) | Specify a mirror. |
| [Example 3](#example3) | Authentication. |


### Property Detail
#### authentications <a name="propertyauthentications"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>WRITE_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Specify authentications to be used in the session.

#### globalSettings <a name="propertyglobalSettings"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>WRITE_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Specify a global settings file to be used in the session.

#### localRepository <a name="propertylocalRepository"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>WRITE_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Specify a local repository to be used in the session.

#### mirrors <a name="propertymirrors"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>WRITE_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Specify mirrors to be used in the session.

#### proxies <a name="propertyproxies"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>WRITE_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Specify proxies to be used in the session.

#### userProperties <a name="propertyuserProperties"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>WRITE_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Specify additional user properties to be set.

#### userSettings <a name="propertyuserSettings"></a>

<table style='font-size:smaller'>
      <tr><td><i>Configured By</i></td><td>ELEMENT</td></tr>
      <tr><td><i>Access</i></td><td>WRITE_ONLY</td></tr>
      <tr><td><i>Required</i></td><td>No.</td></tr>
</table>

Specify a user settings file to be used in the session.


### Examples
#### Example 1 <a name="example1"></a>

Specify a repository.
```xml
                <resolve:resolve id="resolve" noDefaultRepos="true" noSettingsRepos="true" xmlns:resolve="oddjob:resolve">
                    <resolverSession>
                        <resolve:session>
                            <localRepository>
                                <file file="${local.repo}"/>
                            </localRepository>
                        </resolve:session>
                    </resolverSession>
                    <remoteRepositories>
                        <is url="http://localhost:${server.port}"/>
                    </remoteRepositories>
                    <dependencies>
                        <resolve:dependency coords="test.oj.resolve:a:1.2.3"/>
                    </dependencies>
                </resolve:resolve>
```


#### Example 2 <a name="example2"></a>

Specify a mirror.
```xml
                <resolve:resolve id="resolve" xmlns:resolve="oddjob:resolve">
                    <resolverSession>
                        <resolve:session>
                            <userSettings>
                                <file file="${oddjob.dir}/mirror-settings.xml"/>
                            </userSettings>
                        </resolve:session>
                    </resolverSession>
                    <dependencies>
                        <resolve:dependency coords="test.oj.resolve:a:1.2.3"/>
                    </dependencies>
                </resolve:resolve>
```


#### Example 3 <a name="example3"></a>

Authentication.
```xml
                <resolve:resolve id="resolve" noDefaultRepos="true" noSettingsRepos="true" xmlns:resolve="oddjob:resolve">
                    <resolverSession>
                        <resolve:session>
                            <localRepository>
                                <file file="${local.repo}"/>
                            </localRepository>
                        </resolve:session>
                    </resolverSession>
                    <remoteRepositories>
                        <is url="http://localhost:${server.port}">
                            <authentication>
                                <is username="alice" password="secret"/>
                            </authentication>
                        </is>
                    </remoteRepositories>
                    <dependencies>
                        <resolve:dependency coords="test.oj.resolve:a:1.2.3"/>
                    </dependencies>
                </resolve:resolve>
```



-----------------------

<div style='font-size: smaller; text-align: center;'>(c) R Gordon Ltd 2005 - Present</div>
