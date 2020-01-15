# by-chance

A web application built with [Hoplon][hoplon] as part of a dance/music
collaboration between me and [Renay Aumiller][rad].

The collaboration is based on improvising music and dance based on input from
the audience, selected at random by this application.

## Dependencies

- java 1.7+
- [boot][boot]

## Usage
### Development
1. Start the `dev` task. In a terminal run:
    ```bash
    $ boot dev
    ```
    This will give you a  Hoplon development setup with:
    - auto compilation on file changes
    - audible warning for compilation success or failures
    - auto reload the html page on changes
    - Clojurescript REPL

2. Go to http://localhost:8000 in your browser. You should see "Hello, Hoplon!".

3. If you edit and save a file, the task will recompile the code and reload the
   browser to show the updated version.

### Production
1. Run the `prod` task. In a terminal run:
    ```bash
    $ boot prod
    ```

2. The compiled files will be on the `target/` directory. This will use
   advanced compilation and prerender the html.

## License

Copyright © 2018-2020 Dave Yarwood

Distributed under the Eclipse Public License version 2.0.

[boot]: http://boot-clj.com
[hoplon]: http://hoplon.io
[rad]: https://www.radances.com
