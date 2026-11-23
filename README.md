# GestorClientesFX: solución explicada

**DSY1102 Desarrollo Orientado a Objetos · Experiencia de Aprendizaje 3 · Guía 3.2.3: Refactorización de capa DAO e integración de base de datos con interfaz gráfica en JavaFX**

Este documento explica cómo está construida la solución de **GestorClientesFX**, una aplicación de escritorio que **lista, registra, actualiza y elimina clientes guardados en MySQL**. Recorre la arquitectura en capas, el patrón **DAO**, el uso de **JDBC** (`Connection`, `PreparedStatement`, `ResultSet`), el controlador JavaFX y las validaciones.

Léelo con el proyecto abierto en IntelliJ: cada sección indica el archivo y el método del que habla.

---

## Contenido

1. [Qué hace la aplicación](#1-qué-hace-la-aplicación)
2. [Cómo ejecutarla](#2-cómo-ejecutarla)
3. [Estructura del proyecto](#3-estructura-del-proyecto)
4. [Arquitectura en capas](#4-arquitectura-en-capas)
5. [La base de datos](#5-la-base-de-datos)
6. [Modelo: Cliente](#6-modelo-cliente)
7. [Conexión: DatabaseConnection](#7-conexión-databaseconnection)
8. [Capa DAO](#8-capa-dao)
9. [Controlador: ClienteController](#9-controlador-clientecontroller)
10. [Vista: FXML y hoja de estilos](#10-vista-fxml-y-hoja-de-estilos)
11. [Recorrido completo: qué pasa al presionar Guardar](#11-recorrido-completo-qué-pasa-al-presionar-guardar)
12. [Antes y después de la refactorización](#12-antes-y-después-de-la-refactorización)
13. [Errores frecuentes](#13-errores-frecuentes)
14. [Para seguir practicando](#14-para-seguir-practicando)
15. [Preguntas de repaso](#15-preguntas-de-repaso)

---

## 1. Qué hace la aplicación

Una sola ventana con tema oscuro:

| Zona | Qué contiene | Qué hace |
|---|---|---|
| Tabla (centro) | Columnas ID, Nombre, Correo, Ciudad | Muestra los clientes que hay en la base de datos |
| Formulario (derecha) | Campos ID (solo lectura), Nombre, Correo, Ciudad | Al seleccionar una fila, se llena con sus datos |
| Botones | Guardar, Actualizar, Eliminar, Limpiar | Ejecutan las operaciones CRUD |
| Barra de estado (abajo) | Texto | Indica cuántos clientes hay o si no hay conexión |

| Botón | Operación SQL | Cuándo está disponible |
|---|---|---|
| **Guardar** | `INSERT` | Siempre: crea un cliente **nuevo** con los datos del formulario |
| **Actualizar** | `UPDATE` | Solo con un cliente seleccionado |
| **Eliminar** | `DELETE` (pide confirmación) | Solo con un cliente seleccionado |
| **Limpiar** | — | Siempre: vacía el formulario y quita la selección |

Los datos viven en MySQL, así que **siguen ahí al cerrar y volver a abrir** la aplicación.

---

## 2. Cómo ejecutarla

### Requisitos

- JDK 21 e IntelliJ IDEA
- MySQL 8 (o MariaDB) funcionando en `localhost:3306`
- MySQL Workbench (u otro cliente) para ejecutar el script

### Pasos

1. Ejecuta `sql/clientes_fx.sql` completo en MySQL Workbench. Crea la base `clientes_fx`, la tabla `cliente` y tres clientes de prueba.
2. Si tu usuario o contraseña de MySQL no son `root` / `1234`, cámbialos en `database/DatabaseConnection.java`.
3. Abre la carpeta en IntelliJ como proyecto **Maven** y recarga Maven (ícono *Load Maven Changes*) para descargar JavaFX y el conector de MySQL.
4. Ejecuta la clase **`Launcher`**, no `App`.

### ¿Por qué `Launcher` y no `App`?

`App` extiende `Application`, la clase de JavaFX. El proyecto **no tiene `module-info.java`**: JavaFX y el conector de MySQL se cargan desde el *classpath*. En esa situación, si el `main` está en una clase que extiende `Application`, Java se detiene con:

```
Error: JavaFX runtime components are missing, and are required to run this application
```

`Launcher` es una clase común cuyo único trabajo es lanzar `App`:

```java
public class Launcher {
    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}
```

### ¿Por qué no hay `module-info.java`?

El asistente de JavaFX de IntelliJ lo crea, pero se eliminó a propósito. Con módulos, el conector de MySQL no se carga si no se declara explícitamente, y la aplicación falla con *No suitable driver found for jdbc:mysql*. Sin `module-info.java`, todo está en el *classpath* y el conector se registra solo.

### Dependencias (`pom.xml`)

| Dependencia | Versión | Para qué sirve |
|---|---|---|
| `javafx-controls` | 21.0.6 | Controles gráficos: `TableView`, `TextField`, `Button`, `Alert`… |
| `javafx-fxml` | 21.0.6 | Cargar la vista desde `cliente-view.fxml` |
| `mysql-connector-j` | 9.7.0 | Driver JDBC: permite que Java se comunique con MySQL |

---

## 3. Estructura del proyecto

```
GestorClientesFX/
├── pom.xml                                   dependencias (JavaFX + conector MySQL)
├── sql/clientes_fx.sql                       script de la base de datos
└── src/main/
    ├── java/org/example/gestorclientesfx/
    │   ├── Launcher.java                     punto de entrada (main)
    │   ├── App.java                          carga la vista y abre la ventana
    │   ├── model/
    │   │   └── Cliente.java                  una fila de la tabla cliente
    │   ├── database/
    │   │   └── DatabaseConnection.java       URL, usuario y contraseña en un solo lugar
    │   ├── dao/
    │   │   ├── ClienteDao.java               interfaz: el contrato de acceso a datos
    │   │   ├── ClienteDaoMySql.java          implementación con JDBC
    │   │   └── DataAccessException.java      error de datos que el controlador muestra
    │   └── controller/
    │       └── ClienteController.java        eventos, validación y TableView (sin SQL)
    └── resources/org/example/gestorclientesfx/view/
        ├── cliente-view.fxml                 la ventana (Scene Builder)
        └── estilo-oscuro.css                 tema oscuro
```

Cada paquete tiene **una sola responsabilidad**. Esa separación es el objetivo de la guía.

---

## 4. Arquitectura en capas

![Arquitectura en capas: Vista, Controlador, DAO, Conexión y MySQL, con el modelo Cliente compartido](img/arquitectura.png)

| Capa | Paquete | Responsabilidad | Lo que **no** hace |
|---|---|---|---|
| **Vista** | `view` (en `resources`) | Define qué se ve: tabla, campos y botones | No tiene lógica, ni SQL, ni Java |
| **Controlador** | `controller` | Recibe los clics, valida el formulario, actualiza la tabla y muestra mensajes | No escribe SQL ni abre conexiones |
| **DAO** | `dao` | Todo el SQL: listar, insertar, actualizar, eliminar | No muestra mensajes ni toca la interfaz |
| **Conexión** | `database` | Abre conexiones con los datos de acceso | No ejecuta consultas |
| **Modelo** | `model` | Representa un cliente | No sabe de dónde vienen ni a dónde van los datos |

> **Regla de oro:** si ves `PreparedStatement` dentro del controlador, o un `Alert` dentro del DAO, algo quedó en la capa equivocada.

¿Para qué separar? Si cambia la contraseña de la base, se toca **una** clase (`DatabaseConnection`). Si mañana los datos se guardan en Oracle, se escribe **otra** implementación de `ClienteDao` y el controlador no cambia. Si se rediseña la ventana, el SQL queda intacto.

---

## 5. La base de datos

```sql
SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS clientes_fx
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE clientes_fx;

CREATE TABLE IF NOT EXISTS cliente (
    id     INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(120) NOT NULL,
    ciudad VARCHAR(80)  NOT NULL
);

INSERT INTO cliente (nombre, correo, ciudad) VALUES
    ('Camila Rojas',    'camila.rojas@correo.cl',    'Santiago'),
    ('Matías González', 'matias.gonzalez@correo.cl', 'Valparaíso'),
    ('Valentina Soto',  'valentina.soto@correo.cl',  'Concepción');
```

| Elemento | Qué significa |
|---|---|
| `PRIMARY KEY` | `id` identifica a cada cliente: no se repite ni puede ser nulo |
| `AUTO_INCREMENT` | MySQL asigna el `id` automáticamente (1, 2, 3…). Por eso el formulario no lo pide y el `INSERT` no lo envía |
| `VARCHAR(100)` | Texto de largo máximo 100. La validación del formulario respeta estos largos |
| `NOT NULL` | La columna es obligatoria |
| `SET NAMES utf8mb4` y `CHARACTER SET utf8mb4` | Aseguran que tildes y ñ se guarden bien. Sin ellos, desde la consola de MySQL "Matías" puede quedar guardado como "MatÃ­as" |

Cada **fila** de la tabla `cliente` se convierte en un **objeto** `Cliente` en Java, y cada **columna** en un **atributo**. Esa correspondencia es la base de todo lo que sigue.

---

## 6. Modelo: Cliente

`model/Cliente.java` es una clase simple (un *POJO*): atributos privados, constructores, *getters*, *setters* y `toString()`.

```java
public class Cliente {
    private int id;
    private String nombre;
    private String correo;
    private String ciudad;

    public Cliente() { }

    public Cliente(int id, String nombre, String correo, String ciudad) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.ciudad = ciudad;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    // ... resto de getters y setters
}
```

Dos clases lo usan:

- **El DAO** lo **crea** a partir de cada fila del `ResultSet` (lectura) y **lee** sus datos para armar el `INSERT` o el `UPDATE` (escritura).
- **El controlador** lo **muestra** en la tabla y lo **construye** con los datos del formulario.

Los nombres de los *getters* importan: la tabla usa `getNombre()`, `getCorreo()`, etc. para llenar sus columnas (sección 9.2).

---

## 7. Conexión: DatabaseConnection

```java
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/clientes_fx";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private DatabaseConnection() {
        // clase utilitaria: no se crean objetos
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
```

### La URL de conexión

```
jdbc:mysql://localhost:3306/clientes_fx
└┬─┘ └─┬─┘   └───┬───┘ └┬─┘ └────┬────┘
 │     │         │      │        └── nombre de la base de datos
 │     │         │      └── puerto de MySQL
 │     │         └── servidor (este mismo computador)
 │     └── tipo de base de datos (elige el conector de MySQL)
 └── protocolo JDBC
```

### Decisiones de diseño

- **Datos en un solo lugar:** URL, usuario y contraseña son constantes (`private static final`). Ninguna otra clase los conoce.
- **`static`:** se llama `DatabaseConnection.getConnection()` sin crear objetos. El constructor `private` impide escribir `new DatabaseConnection()`.
- **Una conexión nueva por operación:** cada llamada a `getConnection()` abre una conexión, y quien la pide debe cerrarla (sección 8.4).
- **`DriverManager`** busca entre los drivers disponibles uno que entienda `jdbc:mysql:`. Ese driver viene en `mysql-connector-j`. Si falta, aparece *No suitable driver found*.
- **`throws SQLException`:** si no se puede conectar (MySQL apagado, contraseña incorrecta, base inexistente), el error se propaga a quien llamó, es decir, al DAO.

---

## 8. Capa DAO

**DAO** (*Data Access Object*) es un patrón de diseño que concentra en una capa todo el acceso a los datos. El resto de la aplicación pide *"dame los clientes"* o *"guarda este cliente"* sin saber que detrás hay SQL ni MySQL.

![Diagrama de clases: el controlador usa la interfaz ClienteDao, ClienteDaoMySql la implementa y usa DatabaseConnection](img/diagrama-clases.png)

### 8.1 La interfaz: el contrato

```java
public interface ClienteDao {
    List<Cliente> listarTodos();
    void insertar(Cliente cliente);
    void actualizar(Cliente cliente);
    void eliminar(int id);
}
```

La interfaz dice **qué** operaciones existen, no **cómo** se hacen. El controlador declara su atributo con el tipo de la interfaz:

```java
private final ClienteDao clienteDao = new ClienteDaoMySql();
```

Es la **única línea** del controlador que menciona MySQL. Para usar otra base de datos, basta con escribir otra clase que implemente `ClienteDao` y cambiar ese `new`. Esto es **programar contra una interfaz** y es una aplicación directa del **polimorfismo**.

### 8.2 La excepción propia: DataAccessException

```java
public class DataAccessException extends RuntimeException {
    public DataAccessException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
```

En el código de partida de la guía, los errores terminaban en `e.printStackTrace()`: se imprimían en la consola y el usuario nunca se enteraba. En la solución, el DAO **envuelve** la `SQLException` en una `DataAccessException` con un mensaje claro, y el controlador la captura para mostrarla en un `Alert`.

- Extiende **`RuntimeException`** (excepción *unchecked*): así las firmas de `ClienteDao` no necesitan `throws`.
- Guarda la `SQLException` original como **causa**, de modo que el detalle técnico no se pierde.
- El controlador no depende de JDBC: captura `DataAccessException`, no `SQLException`.

### 8.3 Las piezas de JDBC

| Clase | Qué representa | Se obtiene con |
|---|---|---|
| `Connection` | Una conexión abierta con MySQL | `DatabaseConnection.getConnection()` |
| `PreparedStatement` | Una sentencia SQL lista para ejecutarse, con parámetros `?` | `con.prepareStatement(sql)` |
| `ResultSet` | Las filas que devuelve un `SELECT`, recorridas una a una | `ps.executeQuery()` |

| Método | Para qué | Devuelve |
|---|---|---|
| `executeQuery()` | `SELECT` | Un `ResultSet` con las filas |
| `executeUpdate()` | `INSERT`, `UPDATE`, `DELETE` | Un `int`: cuántas filas cambiaron |

### 8.4 Listar: SELECT y mapeo de filas

```java
@Override
public List<Cliente> listarTodos() {
    List<Cliente> clientes = new ArrayList<>();
    String sql = "SELECT id, nombre, correo, ciudad FROM cliente ORDER BY id";

    try (Connection con = DatabaseConnection.getConnection()) {
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            clientes.add(new Cliente(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("correo"),
                    rs.getString("ciudad")
            ));
        }
    } catch (SQLException e) {
        throw new DataAccessException("No se pudieron cargar los clientes.", e);
    }
    return clientes;
}
```

**`try-with-resources`.** Lo que se declara entre los paréntesis del `try`, aquí la `Connection`, **se cierra automáticamente** al terminar el bloque, aunque ocurra un error. El `PreparedStatement` y el `ResultSet` se crean dentro de las llaves. No hace falta cerrarlos aparte: **al cerrarse la conexión, JDBC cierra también todo lo que se creó con ella**.

¿Por qué la conexión sí va en los paréntesis? Si también estuviera dentro de las llaves, habría que cerrarla a mano en un bloque `finally`. Si se olvida, las conexiones quedan abiertas hasta que MySQL rechaza nuevas con *Too many connections*.

**Recorrer el `ResultSet`.** `rs.next()` avanza a la siguiente fila y devuelve `false` cuando no quedan más. `rs.getString("nombre")` lee una columna por su nombre en la fila actual. Convertir cada fila en un objeto `Cliente` se llama **mapear** el resultado.

### 8.5 Insertar, actualizar y eliminar: PreparedStatement

```java
@Override
public void insertar(Cliente cliente) {
    String sql = "INSERT INTO cliente(nombre, correo, ciudad) VALUES (?, ?, ?)";

    try (Connection con = DatabaseConnection.getConnection()) {
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, cliente.getNombre());
        ps.setString(2, cliente.getCorreo());
        ps.setString(3, cliente.getCiudad());
        ps.executeUpdate();
    } catch (SQLException e) {
        throw new DataAccessException("No se pudo guardar el cliente.", e);
    }
}
```

| Operación | SQL | Parámetros |
|---|---|---|
| `insertar` | `INSERT INTO cliente(nombre, correo, ciudad) VALUES (?, ?, ?)` | 1 nombre · 2 correo · 3 ciudad (el `id` lo pone MySQL) |
| `actualizar` | `UPDATE cliente SET nombre = ?, correo = ?, ciudad = ? WHERE id = ?` | 1 nombre · 2 correo · 3 ciudad · **4 id** |
| `eliminar` | `DELETE FROM cliente WHERE id = ?` | 1 id |

Cada `?` es un **parámetro**. Se numeran desde **1**, en el orden en que aparecen en el SQL, y se completan con `setString`, `setInt`, etc. según el tipo de la columna.

> En `actualizar`, el `?` del `WHERE` es el **cuarto**: `ps.setInt(4, cliente.getId())`. Sin `WHERE`, el `UPDATE` cambiaría **todas** las filas de la tabla.

### 8.6 ¿Por qué parámetros y no concatenar texto? Inyección SQL

Compara estas dos formas de armar el mismo `INSERT`:

```java
// ✘ Peligroso: el valor se pega dentro del SQL
String sql = "INSERT INTO cliente(nombre, correo, ciudad) VALUES ('"
        + nombre + "', '" + correo + "', '" + ciudad + "')";

// ✔ Seguro: el valor viaja separado del SQL
String sql = "INSERT INTO cliente(nombre, correo, ciudad) VALUES (?, ?, ?)";
ps.setString(3, ciudad);
```

Si alguien escribe como ciudad `Talca'); DROP TABLE cliente; --`, con la versión concatenada ese texto pasa a **formar parte del SQL** y podría ejecutarse como una orden. Eso es la **inyección SQL**. Con `PreparedStatement`, MySQL recibe el valor como **dato** y guarda exactamente ese texto en la columna `ciudad`, sin ejecutar nada.

Los parámetros también evitan errores con textos que llevan comillas, como "O'Higgins".

---

## 9. Controlador: ClienteController

El controlador es el intermediario entre la vista y el DAO. **No contiene SQL.**

### 9.1 Atributos

```java
@FXML private TableView<Cliente> tablaClientes;
@FXML private TableColumn<Cliente, Integer> colId;
@FXML private TableColumn<Cliente, String> colNombre;
@FXML private TableColumn<Cliente, String> colCorreo;
@FXML private TableColumn<Cliente, String> colCiudad;
@FXML private TextField txtId, txtNombre, txtCorreo, txtCiudad;
@FXML private Button btnActualizar, btnEliminar;
@FXML private Label lblEstado;

private final ClienteDao clienteDao = new ClienteDaoMySql();
private final ObservableList<Cliente> datos = FXCollections.observableArrayList();
```

`@FXML` permite que JavaFX asigne a cada atributo el componente del FXML con el **mismo `fx:id`**, aunque el atributo sea `private`. Si el nombre no coincide, el atributo queda en `null` y aparece un `NullPointerException` al usarlo.

### 9.2 `initialize()`: preparar la ventana

```java
@FXML
public void initialize() {
    colId.setCellValueFactory(new PropertyValueFactory<>("id"));
    colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
    colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
    colCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudad"));

    tablaClientes.setItems(datos);

    // Al seleccionar una fila (con el mouse o con las flechas) se copia al formulario
    tablaClientes.getSelectionModel().selectedItemProperty()
            .addListener((obs, anterior, nuevo) -> seleccionarCliente());

    // Actualizar y Eliminar solo tienen sentido con un cliente seleccionado
    btnActualizar.disableProperty().bind(txtId.textProperty().isEmpty());
    btnEliminar.disableProperty().bind(txtId.textProperty().isEmpty());

    // Se carga después de mostrar la ventana: si MySQL no responde, el aviso aparece sobre ella
    Platform.runLater(this::cargarClientes);
}
```

JavaFX llama a `initialize()` automáticamente, después de crear los componentes del FXML. Ahí se hace toda la preparación:

| Línea | Qué hace |
|---|---|
| `new PropertyValueFactory<>("nombre")` | La columna llama a `getNombre()` de cada `Cliente`. Si el texto no coincide con un *getter*, la columna aparece vacía |
| `tablaClientes.setItems(datos)` | La tabla muestra la `ObservableList`. Se hace **una sola vez** |
| `selectedItemProperty().addListener(...)` | Cada vez que cambia la fila seleccionada, se llena el formulario |
| `disableProperty().bind(...)` | **Enlace (*binding*)**: los botones se deshabilitan solos mientras `txtId` esté vacío |
| `Platform.runLater(this::cargarClientes)` | Carga los datos justo después de mostrar la ventana |

**¿Por qué una `ObservableList`?** Es una lista que avisa cuando cambia. La tabla está conectada a ella, así que cada `setAll(...)` se ve en pantalla de inmediato, sin recargar la ventana.

**¿Por qué `Platform.runLater`?** `initialize()` se ejecuta *antes* de que la ventana aparezca. Si MySQL no responde y la carga se hiciera ahí mismo, el aviso de error saldría antes que la ventana y sin su tema. Con `runLater`, la ventana se muestra primero y el aviso aparece sobre ella.

### 9.3 Cargar y seleccionar

```java
private void cargarClientes() {
    try {
        List<Cliente> clientes = clienteDao.listarTodos();
        datos.setAll(clientes);
        lblEstado.setText(clientes.size() + (clientes.size() == 1 ? " cliente registrado" : " clientes registrados"));
    } catch (DataAccessException e) {
        datos.clear();
        lblEstado.setText("Sin conexión con la base de datos");
        mostrarError(e);
    }
}

@FXML
private void seleccionarCliente() {
    Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();
    if (seleccionado != null) {
        txtId.setText(String.valueOf(seleccionado.getId()));
        txtNombre.setText(seleccionado.getNombre());
        txtCorreo.setText(seleccionado.getCorreo());
        txtCiudad.setText(seleccionado.getCiudad());
    }
}
```

`cargarClientes()` se llama al iniciar y **después de cada operación**: así la tabla siempre muestra lo que realmente hay en la base. Si falla la conexión, la aplicación **no se cierra**: vacía la tabla, informa en la barra de estado y muestra el error.

### 9.4 Validar el formulario

Antes de guardar o actualizar, `formularioValido()` revisa los campos **en orden** y se detiene en el primer error: muestra un `Alert` de advertencia y deja el cursor en el campo con problemas.

```java
private static final Pattern FORMATO_CORREO =
        Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

private boolean formularioValido() {
    String nombre = txtNombre.getText().trim();
    String correo = txtCorreo.getText().trim();
    String ciudad = txtCiudad.getText().trim();

    if (nombre.isEmpty()) {
        return invalido(txtNombre, "Ingrese el nombre del cliente.");
    }
    // ... largo del nombre, correo vacío, formato y largo del correo, ciudad ...
    return true;
}

private boolean invalido(TextField campo, String mensaje) {
    mostrarAlerta(Alert.AlertType.WARNING, "Datos no válidos", mensaje);
    campo.requestFocus();
    return false;
}
```

| Campo | Regla | Mensaje |
|---|---|---|
| Nombre | Obligatorio | Ingrese el nombre del cliente. |
| Nombre | Máximo 100 caracteres | El nombre no puede superar los 100 caracteres. |
| Correo | Obligatorio | Ingrese el correo del cliente. |
| Correo | Formato `texto@dominio.ext` | El correo no tiene un formato válido (ejemplo: nombre@dominio.cl). |
| Correo | Máximo 120 caracteres | El correo no puede superar los 120 caracteres. |
| Ciudad | Obligatoria | Ingrese la ciudad del cliente. |
| Ciudad | Máximo 80 caracteres | La ciudad no puede superar los 80 caracteres. |

- **`.trim()`** quita los espacios del inicio y del final: un campo con solo espacios cuenta como vacío.
- **Los largos máximos coinciden con los `VARCHAR`** de la tabla. Si se permitiera más, MySQL rechazaría la operación con *Data too long for column*.
- **La expresión regular** del correo exige: uno o más caracteres válidos, `@`, un dominio y al menos un `.extensión`.

Validar **antes** de llamar al DAO evita viajes inútiles a la base y da mensajes claros al usuario.

### 9.5 Guardar, actualizar, eliminar y limpiar

```java
@FXML
private void guardarCliente() {
    if (!formularioValido()) {
        return;
    }
    Cliente cliente = new Cliente(
            0, // el id lo asigna MySQL (AUTO_INCREMENT)
            txtNombre.getText().trim(),
            txtCorreo.getText().trim(),
            txtCiudad.getText().trim()
    );
    try {
        clienteDao.insertar(cliente);
        cargarClientes();
        limpiarFormulario();
        mostrarAlerta(Alert.AlertType.INFORMATION, "Cliente guardado",
                cliente.getNombre() + " se registró correctamente.");
    } catch (DataAccessException e) {
        mostrarError(e);
    }
}
```

Todas las operaciones siguen el mismo patrón:

> **validar → construir el `Cliente` → llamar al DAO → `cargarClientes()` → limpiar → avisar**

| Método | Diferencias con `guardarCliente()` |
|---|---|
| `actualizarCliente()` | Primero verifica que haya un cliente seleccionado. Construye el `Cliente` con `Integer.parseInt(txtId.getText())` en vez de `0`, y llama a `clienteDao.actualizar(...)` |
| `eliminarCliente()` | No valida el formulario: usa la fila seleccionada y **pide confirmación** antes de llamar a `clienteDao.eliminar(id)` |
| `limpiarFormulario()` | Vacía los cuatro campos y quita la selección (`clearSelection()`). Como `txtId` queda vacío, el *binding* deshabilita Actualizar y Eliminar |

> **Guardar siempre crea un cliente nuevo.** Si hay una fila seleccionada y presionas Guardar en vez de Actualizar, se inserta una **copia** con un `id` nuevo. Para modificar un cliente existente se usa Actualizar.

### 9.6 Confirmación antes de eliminar

```java
Alert confirmacion = crearAlerta(Alert.AlertType.CONFIRMATION, "Eliminar cliente",
        "¿Eliminar a " + seleccionado.getNombre() + "? Esta acción no se puede deshacer.");
ButtonType botonEliminar = new ButtonType("Eliminar", ButtonBar.ButtonData.OK_DONE);
ButtonType botonCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
confirmacion.getButtonTypes().setAll(botonEliminar, botonCancelar);

Optional<ButtonType> respuesta = confirmacion.showAndWait();
if (respuesta.isEmpty() || respuesta.get() != botonEliminar) {
    return; // el usuario canceló o cerró el diálogo
}
```

- **`showAndWait()`** detiene el método hasta que el usuario responde.
- Devuelve un **`Optional<ButtonType>`**: puede estar vacío si el usuario cierra el diálogo con la X. Por eso se revisa `isEmpty()` antes de `get()`.
- Los botones se crean **en español**. Los botones predeterminados de JavaFX dependen del idioma del sistema operativo y en algunos equipos aparecen como "OK" y "Cancel".

### 9.7 Mensajes al usuario: Alert

```java
private void mostrarError(DataAccessException e) {
    String detalle = e.getCause() != null ? e.getCause().getMessage() : "";
    mostrarAlerta(Alert.AlertType.ERROR, "Error de base de datos",
            e.getMessage() + "\nRevise que MySQL esté activo y que exista la base clientes_fx.\n\nDetalle: " + detalle);
}

private Alert crearAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
    Alert alerta = new Alert(tipo);
    alerta.setTitle(titulo);
    alerta.setHeaderText(null);
    alerta.setContentText(mensaje);
    alerta.getButtonTypes().setAll(new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE));
    alerta.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // muestra el mensaje completo, sin "..."
    // ... aplica la misma hoja de estilos oscura de la ventana
    return alerta;
}
```

| Tipo de `Alert` | Cuándo se usa |
|---|---|
| `INFORMATION` | Cliente guardado o actualizado |
| `WARNING` | Dato no válido en el formulario, o acción sin cliente seleccionado |
| `CONFIRMATION` | Antes de eliminar |
| `ERROR` | Falla de la base de datos (`DataAccessException`) |

`mostrarError` usa los dos mensajes de la excepción: el **propio** ("No se pudo guardar el cliente.") y el de la **causa** (la `SQLException` original), para que se sepa qué ocurrió y por qué.

---

## 10. Vista: FXML y hoja de estilos

`cliente-view.fxml` se diseña en **Scene Builder**. Su raíz es un `BorderPane`:

| Región | Contenido |
|---|---|
| `top` | Título "Gestor de Clientes" |
| `center` | `TableView` con cuatro columnas |
| `right` | Formulario (`GridPane`) y los cuatro botones |
| `bottom` | Barra de estado (`lblEstado`) |

Tres atributos conectan la vista con el controlador:

| Atributo | En Scene Builder | Qué hace |
|---|---|---|
| `fx:controller="org.example.gestorclientesfx.controller.ClienteController"` | Panel *Controller* → *Controller class* | Indica qué clase controla la vista. Sin él, los `onAction` fallan con *No controller specified* |
| `fx:id="txtNombre"` | *Code* → *fx:id* | Conecta el componente con el atributo `@FXML` del mismo nombre |
| `onAction="#guardarCliente"` | *Code* → *On Action* | Al hacer clic, ejecuta ese método del controlador |

| Componente | `fx:id` | `onAction` |
|---|---|---|
| Tabla | `tablaClientes` | |
| Columnas | `colId`, `colNombre`, `colCorreo`, `colCiudad` | |
| Campos | `txtId` (no editable), `txtNombre`, `txtCorreo`, `txtCiudad` | |
| Guardar | | `guardarCliente` |
| Actualizar | `btnActualizar` | `actualizarCliente` |
| Eliminar | `btnEliminar` | `eliminarCliente` |
| Limpiar | | `limpiarFormulario` |
| Barra de estado | `lblEstado` | |

El **tema oscuro** está en `estilo-oscuro.css`, asociado a la raíz del FXML (*Properties* → *Stylesheets*). Solo define colores, bordes y tamaños: fondo, paneles, tabla, botones y diálogos. Separar el estilo del FXML es lo mismo que separar el SQL del controlador: cada archivo tiene una sola responsabilidad.

---

## 11. Recorrido completo: qué pasa al presionar Guardar

![Secuencia al presionar Guardar: la vista llama al controlador, este valida y usa el DAO, que obtiene una conexión y ejecuta el INSERT en MySQL](img/flujo-guardar.png)

1. El usuario completa el formulario y presiona **Guardar**. `onAction="#guardarCliente"` ejecuta el método en el controlador.
2. `formularioValido()` revisa nombre, correo y ciudad. Si algo falla, muestra una advertencia y el método termina.
3. El controlador crea un `Cliente` con `id = 0`, porque el `id` lo asigna MySQL.
4. Llama a `clienteDao.insertar(cliente)`. Desde aquí, el controlador no sabe nada de SQL.
5. El DAO pide una conexión a `DatabaseConnection`, que usa `DriverManager` con la URL, el usuario y la contraseña.
6. El DAO prepara el `INSERT` con tres `?`, les asigna los valores con `setString` y lo ejecuta con `executeUpdate()`. MySQL asigna el `id`.
7. Al terminar el `try`, la conexión se cierra sola, y con ella el `PreparedStatement`.
8. De vuelta en el controlador, `cargarClientes()` vuelve a leer todos los clientes, y `datos.setAll(...)` actualiza la tabla.
9. El formulario se limpia y aparece el aviso "Cliente guardado".

Si en los pasos 5 o 6 ocurre un error, el DAO lanza `DataAccessException`, el controlador la captura y muestra el error **sin cerrar la aplicación**.

---

## 12. Antes y después de la refactorización

El objetivo de la guía es pasar de un controlador que hace todo a una aplicación por capas.

**Antes**, el controlador abría la conexión, armaba el SQL y llenaba la tabla, todo en el mismo método:

```java
// Código de partida: SQL y JDBC dentro del controlador
@FXML
private void cargarClientes() {
    String url = "jdbc:mysql://localhost:3306/clientes_fx";
    try (Connection con = DriverManager.getConnection(url, "root", "1234");
         PreparedStatement ps = con.prepareStatement("SELECT * FROM cliente");
         ResultSet rs = ps.executeQuery()) {
        // ... recorre rs y llena la tabla
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
```

**Después**, el controlador solo delega:

```java
List<Cliente> clientes = clienteDao.listarTodos();
datos.setAll(clientes);
```

| Aspecto | Antes (todo en el controlador) | Después (con capa DAO) |
|---|---|---|
| Dónde está el SQL | En los métodos `@FXML` | Solo en `ClienteDaoMySql` |
| URL, usuario y contraseña | Repetidos en cada método | Una vez, en `DatabaseConnection` |
| Qué conoce el controlador | JDBC, SQL y JavaFX | La interfaz `ClienteDao` y JavaFX |
| Errores de base de datos | `printStackTrace()`: el usuario no se entera | `DataAccessException` → `Alert` con el detalle |
| Cambiar de base de datos | Reescribir el controlador | Crear otra implementación de `ClienteDao` |
| Probar el acceso a datos | Solo abriendo la ventana | Con un `main` de prueba, sin interfaz |

Una forma rápida de probar la capa de datos sin la ventana:

```java
public class PruebaDao {
    public static void main(String[] args) {
        ClienteDao dao = new ClienteDaoMySql();
        System.out.println(dao.listarTodos());
    }
}
```

Si imprime los tres clientes del script, la conexión y el DAO funcionan. Si falla aquí, el problema no está en JavaFX.

---

## 13. Errores frecuentes

| Síntoma o mensaje | Causa probable | Solución |
|---|---|---|
| `Communications link failure` o `Connection refused` | MySQL no está iniciado, o el puerto no es 3306 | Inicia el servicio de MySQL (en Windows: *Servicios* → *MySQL80*) y revisa el puerto en la URL |
| `Access denied for user 'root'@'localhost'` | Usuario o contraseña incorrectos | Corrige `USER` y `PASSWORD` en `DatabaseConnection` |
| `Unknown database 'clientes_fx'` | No se ejecutó el script | Ejecuta `sql/clientes_fx.sql` completo |
| `Table 'clientes_fx.cliente' doesn't exist` | Se creó la base pero no la tabla | Ejecuta el `CREATE TABLE` del script |
| `No suitable driver found for jdbc:mysql` | Falta el conector, o hay un `module-info.java` | Recarga Maven, revisa que `mysql-connector-j` esté en *Dependencies* y elimina `module-info.java` |
| `JavaFX runtime components are missing` | Se ejecutó `App` en vez de `Launcher` | Ejecuta `Launcher` |
| `Location is not set` | La ruta del FXML en `App` no coincide con la carpeta | El archivo debe estar en `resources/org/example/gestorclientesfx/view/` |
| `No controller specified` | Falta `fx:controller` en la raíz del FXML | Asígnalo en el panel *Controller* de Scene Builder |
| Una columna de la tabla aparece vacía | El texto de `PropertyValueFactory` no coincide con un *getter* | `"nombre"` necesita `getNombre()`: revisa mayúsculas y nombres |
| `NullPointerException` en el controlador | Un `fx:id` no coincide con su atributo `@FXML` | Compara nombre por nombre con la tabla de la sección 10 |
| Actualizar no cambia nada | El `WHERE id = ?` recibe un `id` que no existe | Revisa `ps.setInt(4, cliente.getId())` y que `txtId` tenga el `id` de la fila |
| `Data too long for column` | El texto supera el largo del `VARCHAR` | La validación debe revisar los largos máximos |
| Tildes y ñ aparecen como `Ã­` o `?` | El script se ejecutó sin `SET NAMES utf8mb4` | Borra la base y ejecuta de nuevo el script completo |
| `Too many connections` | Conexiones que nunca se cierran | Declara la `Connection` en los paréntesis del `try` |

---

## 14. Para seguir practicando

1. **Búsqueda por nombre:** agrega a `ClienteDao` el método `List<Cliente> buscarPorNombre(String texto)` con `SELECT ... WHERE nombre LIKE ?` y el parámetro `"%" + texto + "%"`. Agrega un campo de búsqueda en la vista.
2. **Filtro por ciudad:** igual que el anterior, con `WHERE ciudad = ?`, y un `ComboBox` con las ciudades.
3. **Correo único:** agrega `UNIQUE` a la columna `correo` y muestra un mensaje claro cuando MySQL rechace un correo repetido.
4. **Confirmar la actualización:** usa el `int` que devuelve `executeUpdate()` para saber si el `UPDATE` o el `DELETE` afectaron alguna fila, y avisa si no.
5. **Otra implementación:** escribe `ClienteDaoMemoria`, que guarde los clientes en un `ArrayList`, y cambia solo la línea `new ClienteDaoMySql()` del controlador. La aplicación debe seguir funcionando.

---

## 15. Preguntas de repaso

1. ¿Qué problema resuelve el patrón DAO? ¿Qué pasaría con el controlador si no existiera?
2. ¿Por qué el atributo del controlador es de tipo `ClienteDao` y no `ClienteDaoMySql`?
3. ¿Qué diferencia hay entre `executeQuery()` y `executeUpdate()`?
4. ¿Qué cierra automáticamente el `try-with-resources` del DAO? ¿Qué pasa con el `PreparedStatement` y el `ResultSet`?
5. Explica con un ejemplo qué es la inyección SQL y cómo la evita `PreparedStatement`.
6. En el `UPDATE`, ¿qué número de parámetro corresponde al `id` y por qué?
7. ¿Por qué el formulario no pide el `id` al registrar un cliente?
8. ¿Para qué sirve `DataAccessException` si ya existe `SQLException`?
9. ¿Por qué la tabla se actualiza sola después de `datos.setAll(...)`?
10. ¿Cómo logra la aplicación que Actualizar y Eliminar se habiliten solo con una fila seleccionada?
11. ¿Por qué la carga inicial usa `Platform.runLater` en vez de llamarse directo en `initialize()`?
12. Si cambia la contraseña de MySQL, ¿cuántas clases hay que modificar? ¿Cuáles?
