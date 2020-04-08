# vedrax-web

-   [Vedrax Descriptor](#Descriptor)
-   [Vedrax JPA](#Jpa)
-   [Vedrax Math](#Math)
-   [Vedrax Security](#Security)
-   [Vedrax Util](#Util)
-   [Build and Install locally](#Build)

## <a name='Descriptor'>Vedrax Descriptor</a>
The `FormGenerator` interface is used to generate form descriptor using POJO 
and specific annotations.

The available annotations are:
* `Actions`: list of `Action` 
* `Action`: describes an action as 'redirect','select' or 'form' with label, url and fragment if any
* `Children`: describes the children objects as a class name
* `Component`: describes the component type available in `com.vedrax.descriptor.enums.ControlType`
* `Groups`: list of `Group`
* `Group`: describes a `Group` by its name and a list of attributes keys
* `Lov`: references a static list of values by its `Enum` name
* `Properties`: list of `Property`
* `Property`: describes an input property by its name and value

The available descriptors are:
* `ActionDescriptor`: describes an action at the column level of a table
* `ColumnDescriptor`: describes a column in a table
* `FormControlDescriptor`: describes a form control
* `FormDescriptor`: describes the entire form
* `FormGroupDescriptor`: describes group of controls in a form
* `OptionDescriptor`: describes an option for a multi choice component
* `PropertyDescriptor`: describes the input property
* `TableDescriptor`: describes a table
* `ValidationDescriptor`: describes a form validation

## <a name='Jpa'>Vedrax JPA</a>
This module provides the following utilities:
* `ExtendedRepository`: used for extending repository
* `JpaCriteriaHelper`: used for helping in the use of Criteria query

## <a name='Math'>Vedrax Math</a>
This module provides a Math expression parser by using the MathJS library

* `MathJS`: provides a powerful math expression parser
* `IMatrix`: provides an interface dealing with matrix data type

## <a name='Security'>Vedrax Security</a>
This module provides the following utilities:
* `AuditorAwareImpl`: provides Spring auditor capabilities
* `AuthenticationFilter`,`AuthenticationProvider` and `SecurityConfig`: provide authentication capabilities
* `TokenService`: deals with JWT

## <a name='Util'>Vedrax Util</a>
This module provides the following utilities:
* `BeanUtil`
* `DateUtil`
* `DBIntegrationUtils`: utilities for DB integration testing
* `GCPUtil`: Google Cloud Platform utilities
* `JsonUtils`
* `NumUtils`: number utilities
* `ResourceUtils`
* `ServletUtils`

## <a name='Build'>Build and Install locally</a>
run the following command at the parent level:

`mvn clean install`

The modules will be install to your local repository.

You can also install as a 3rd library:

* run `mvn clean package` at the module level
* run `cd target`
* run `mvn install:install-file -Dfile=vedrax-descriptor-1.0.jar -DpomFile=pom.xml`
