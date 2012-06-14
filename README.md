# GuiceUnit


Use Google Guice injections in your JUnit 4 tests with least possible hassle.

GuiceUnit allows you to inject objects in fields and static variables in your unit tests.
It automatically scans your classpath for Guice modules, creates injector and executes test
with all objects in place.

It allows you to overwrite your production bindings with test modules using single annotation.
You can also declare module that should be used in current test class that will override other bindings or with all other modules disabled.

It's all very simple. Just annotate your test class with `@RunWith(GUnitTestRunner.class)` and it's enough to
get all fields injected from your production modules.

It also provides few general Guice helpers in case you want to use it in more declarative way or scan classpath for
modules or static injects. Read along if you're interested.

## Injecting values for fields and statics
After you declare yout JUnit test to be executed with `GUnitTestRunner` all your `@Inject` fields
will get values from modules available in classpath.

GuiceUnit uses great [Reflections](https://code.google.com/p/reflections/ "Reflections") library to scan Java classpath and find all classes with *Module* in name and extending `AbstractModule` or implementing `Module` from
Guice. All these are then used to create `Injector` and inject into annotated fields and static variables in your code.

**Example:**

```java
@RunWith(GUnitTestRunner.class)
public class InjectionsTest {

    @Inject
    @Named("static")
    private static String s;
 
    @Inject
    @Named("field")
    private String f;
}
```

## Overriding bindings from production modules

GuiceUnit declares few annotations that allow you to override bindings depending on application runtime mode.
These are: `@MasterModule`, `@TestModule` and `@DevelopmentModule`.

Modules without any annotation will be available in all modes. They are considered your production modules. You usually shouldn't need to declare them in your tests, they should come from your production code.
Special case is the `@MasterModule` - this one is not really test related. There can be only one `@MasterModule`. It will override all other production modules.

`@TestModule` - these are loaded only if application is running in TEST mode - default when you execute a JUnit test annotated with `@RunWith(GUnitTestRunner.class)`. They will override all bindings from production modules (also ones from master module).

`@DevelopmentModule` - these are loaded only if you execute your application in development mode (you have to create injector using `InjectorFactory.createInjector` to make any use of it). They will override all bindings from production modules (also ones from master module).

There is also some special annotation you can put on your test class `@WithModule` and declare special module that should override all others only for this test class. It will be described in more detail later, just noted it here because it can change bindings scope.

If you're still not sure what bindings you'll get here's from where bindings will be taken by priority:

1. `@WithModule`
2. `@TestModule` *OR* `@DevelopmentModule`
3. `MasterModule`
4. no annotation

## Run test with module specified

Using `@WithModule` annotation you can declare specific module that should either override all other bindings or
be the only one used to configure injector. It's little bit tricky to use, but allows you to create unit tests with individual bindings.

It can be used in 2 modes (declared in annotation override attribute):

* All modules (override = `true`) - Injector will have all bindings found in classpath loaded according to previous rules overridden by bindings from declared module. All static injections are performed.

* Single module (override = `false`) - in this mode injector is created only with declared module bindings. That is, you won't get any other bindings. Make sure you don't have any `@Inject` declarations that are not handled by declared module bindings. This mode is also different in one more case: it does not invoke static injections outside current test class.

## Examples
Take a look at tests to see some examples of use.
