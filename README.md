# Ejercicio Microempresa — Guía paso a paso (POO en Java)

Este README explica, paso a paso, cómo construir el ejercicio **Microempresa**. La idea central es aplicar el orden natural de diseño en POO: primero se modelan las clases **independientes** (sin colaboración), luego las clases que **colaboran** entre sí, después la clase que **colecciona** objetos, y finalmente se arma el **main** que orquesta todo.

## Orden de construcción

1. `Cliente` (independiente)
2. `Producto` (independiente)
3. `Solicitud` (colabora con `Cliente` y usa `Producto`)
4. `ColeccionSolicitudes` (colecciona objetos `Solicitud`)
5. `Microempresa` (clase main, instancia y orquesta todo)

---

## Paso 1: Clase `Cliente` (independiente)

`Cliente` no depende de ninguna otra clase del modelo, por eso se construye primero. Representa a la persona que hace un pedido.

### 1.1 Atributos encapsulados

Todos los atributos son `private`: eso es la **encapsulación**, nadie fuera de la clase puede tocarlos directamente, solo a través de getters/setters.

```java
public class Cliente {
    private int run, telefono;
    private String nombre, mail, direccion;
    private char dv;
}
```

### 1.2 Constructores

Se recomienda siempre dejar **dos constructores**: uno vacío (para poder crear el objeto y setear después) y uno con todos los parámetros (para crear el objeto completo de una vez).

```java
public Cliente() {
}

public Cliente(int run, int telefono, String nombre, String mail, String direccion, char dv) {
    this.run = run;
    this.telefono = telefono;
    this.nombre = nombre;
    this.mail = mail;
    this.direccion = direccion;
    this.dv = dv;
}
```

### 1.3 Accesadores (getters) y mutadores (setters)

Por cada atributo privado se crea un getter y un setter. Esto es lo que permite acceder y modificar el estado del objeto de forma controlada.

```java
public int getRun() {
    return run;
}

public void setRun(int run) {
    this.run = run;
}

// ... el mismo patrón se repite para telefono, nombre, mail, direccion, dv
```

### 1.4 Métodos "customer" (comportamiento propio del objeto)

Se agrupan bajo el nombre de métodos "customer" los métodos que no son ni constructor ni get/set, y que le dan comportamiento propio a la clase. El más básico y casi obligatorio es `toString()`, que sirve para representar el objeto como texto (muy útil para depurar con `System.out.println`).

```java
@Override
public String toString() {
    return "Cliente{" +
            "run=" + run +
            ", telefono=" + telefono +
            ", nombre='" + nombre + '\'' +
            ", mail='" + mail + '\'' +
            ", direccion='" + direccion + '\'' +
            ", dv=" + dv +
            '}';
}
```

**⚠️Punto clave⚠️:** `Cliente` no tiene ningún atributo cuyo tipo sea otra clase del proyecto (`Producto`, `Solicitud`, etc.). Por eso decimos que es una clase independiente.

---

## Paso 2: Clase `Producto` (independiente)

Igual que `Cliente`, `Producto` tampoco depende de otras clases del modelo, así que puede construirse en paralelo o inmediatamente después.

### 2.1 Atributos encapsulados

```java
public class Producto {
    private int codigo, precio, cantidad;
    private String subcategoria, descripcion;
    private char medida; // U: unidad, G: gramos
}
```

Nota el atributo `medida`: es un `char` que solo admite dos valores válidos conceptualmente (`'U'` o `'G'`). Esto se retoma más adelante en `Solicitud`.

### 2.2 Constructores

```java
public Producto() {
}

public Producto(int codigo, int precio, int cantidad, String subcategoria, String descripcion, char medida) {
    this.codigo = codigo;
    this.precio = precio;
    this.cantidad = cantidad;
    this.subcategoria = subcategoria;
    this.descripcion = descripcion;
    this.medida = medida;
}
```

### 2.3 Accesadores y mutadores (con validación)

Aquí conviene destacar un caso especial: el setter de `subcategoria` no solo asigna el valor, sino que **valida** que sea `"FRUTA"` o `"VERDURA"` antes de aceptarlo. Si no cumple, lanza una excepción. Esto es un buen ejemplo de que el setter puede proteger la integridad del objeto, no solo asignar memoria.

```java
public void setSubcategoria(String subcategoria) {
    if (!subcategoria.equalsIgnoreCase("FRUTA") &&
            !subcategoria.equalsIgnoreCase("VERDURA")) {
        throw new IllegalArgumentException("Subcategoría Incorrecta");
    }
    this.subcategoria = subcategoria;
}
```

> En el `main`, esta validación se ve en acción cuando se intenta crear un producto con categoría `"hortaliza"` dentro de un bloque `try/catch`: como no es ni `"FRUTA"` ni `"VERDURA"`, se captura la excepción y se imprime el mensaje de error.

El resto de los atributos (`codigo`, `precio`, `cantidad`, `descripcion`, `medida`) tienen getters y setters simples, sin validación adicional.

### 2.4 Método `toString()`

```java
@Override
public String toString() {
    return "Producto{" +
            "codigo=" + codigo +
            ", precio=" + precio +
            ", cantidad=" + cantidad +
            ", subcategoria='" + subcategoria + '\'' +
            ", descripcion='" + descripcion + '\'' +
            ", medida=" + medida +
            '}';
}
```

---

## Paso 3: Clase `Solicitud` (colaboración con `Cliente`, uso de `Producto`)

`Solicitud` es la primera clase que **depende** de otras. Aquí se introduce el concepto de **colaboración**.

### 3.1 Atributos, incluido el atributo de colaboración

```java
public class Solicitud {
    private int numero, total;
    private Date fecha;
    private Cliente cliente;       // <-- ATRIBUTO DE COLABORACIÓN
    private String productos = ""; // acumula el detalle de productos comprados
}
```

**Explicación de la colaboración:** el atributo `cliente` es de tipo `Cliente`, es decir, `Solicitud` "colabora" con `Cliente` porque un objeto `Solicitud` necesita un objeto `Cliente` completo para existir con sentido (no tendría lógica una solicitud sin saber quién la hizo). Esto es distinto a una simple relación de "uso": aquí `Solicitud` **contiene una referencia permanente** a `Cliente` como parte de su estado.

Nota que `productos` es un `String` acumulador, no una lista de objetos `Producto`. Esto es intencional para este ejercicio y se explica en el punto 3.4.

### 3.2 Constructores

```java
public Solicitud() {
}

public Solicitud(int numero, int total, Date fecha, Cliente cliente, String productos) {
    this.numero = numero;
    this.total = total;
    this.fecha = fecha;
    this.cliente = cliente;
    this.productos = productos;
}
```

### 3.3 Accesadores y mutadores

Igual patrón que las clases anteriores: un getter y un setter por atributo, incluyendo `getCliente()` / `setCliente(Cliente cliente)`, que es la puerta de entrada para la colaboración.

```java
public Cliente getCliente() {
    return cliente;
}

public void setCliente(Cliente cliente) {
    this.cliente = cliente;
}
```

### 3.4 El método `agregarProducto()`: cómo se usa el objeto `Producto` aquí

Este es un punto muy importante para ⚠️ detenernos y explicar ⚠️, porque `Producto` **no se guarda como objeto** dentro de `Solicitud`. En lugar de eso, `agregarProducto()` recibe el objeto `Producto` **temporalmente**, le "extrae" los datos que necesita (precio, cantidad, descripción, medida) y los usa para:

1. Calcular cuánto suma este producto al `total` de la solicitud.
2. Construir una línea de texto que se agrega al `String productos`.

Después de ejecutar el método, el objeto `Producto` en sí **no queda referenciado** por `Solicitud` — solo queda su "👣huella👣" en el texto y en el total. Esto es distinto de la colaboración con `Cliente`, donde sí se guarda la referencia al objeto completo.

```java
public void agregarProducto(Producto producto, int cantidad) {
    String unidadMedida;
    if (producto.getMedida() == 'U') {
        unidadMedida = "unidad(es)";
        this.total += producto.getPrecio() * cantidad;
    } else {
        unidadMedida = "gramos";
        this.total += (int) ((producto.getPrecio() * cantidad) / producto.getCantidad());
    }
    // arma el String de productos
    this.productos += producto.getDescripcion() +
            " $" + producto.getPrecio() +
            " " + cantidad +
            " " + unidadMedida + ";\n";
}
```

**⚠️Diferencia clave⚠️:**

| Relación | Clase involucrada | ¿Se guarda el objeto? | ¿Cómo se usa? |
|---|---|---|---|
| Colaboración | `Cliente` | Sí, como atributo (`this.cliente = cliente`) | Se consulta en cualquier momento, ej. `verSolicitud()` llama a `this.cliente.getNombre()` |
| Uso puntual | `Producto` | No, solo se recibe como parámetro | Se leen sus datos una sola vez dentro de `agregarProducto()` y se descartan como objeto |

También vale la pena explicar la lógica de negocio dentro del `if`: si la medida es `'U'` (unidad), el precio ya representa el valor de una unidad completa, así que se multiplica directo por la cantidad pedida. Si la medida es `'G'` (gramos), el precio registrado en `Producto` corresponde al paquete completo (`cantidad` de `Producto`), así que hay que sacar un prorrateo: `(precio * cantidadPedida) / cantidadDelPaquete`.

### 3.5 Otros métodos customer

```java
public int descontar(int porcentaje) {
    return (int) (this.total * porcentaje / 100);
}

public int obtenerNroSolicitud() {
    this.numero += 1;
    return this.numero;
}

public void verSolicitud() {
    SimpleDateFormat formated = new SimpleDateFormat("dd-MMM-yyyy");
    System.out.println("Solicitud: " + this.numero);
    System.out.println("***************************");
    System.out.println("Cliente: " + this.cliente.getNombre()); // <-- aquí se usa la colaboración
    System.out.println("Fecha: " + formated.format(this.fecha));
    System.out.println(this.productos);
    System.out.println("***************************");
    System.out.println("Total: $" + this.total + "\n");
}
```

Fíjate que `verSolicitud()` es la prueba concreta de la colaboración: para imprimir el nombre del cliente, `Solicitud` le pregunta directamente a su atributo `cliente` (`this.cliente.getNombre()`), en vez de guardar el nombre como un `String` suelto.

---

## Paso 4: Clase `ColeccionSolicitudes` (clase colección)

Una vez que `Solicitud` está lista, se construye la clase que administra **muchas** solicitudes a la vez. Su único atributo es una estructura de datos (`ArrayList`) que guarda objetos `Solicitud`.

### 4.1 Atributo y constructor

```java
public class ColeccionSolicitudes {
    private ArrayList<Solicitud> solicitudes;

    public ColeccionSolicitudes() {
        solicitudes = new ArrayList<Solicitud>();
    }
}
```

Aquí no hay getters/setters tradicionales por atributo simple, porque el "atributo" es una colección completa; en cambio se ofrecen **métodos de gestión** de esa colección.

### 4.2 Métodos de gestión de la colección

```java
public void agregarSolicitud(Solicitud solicitud) {
    solicitudes.add(solicitud);
    System.out.println("Solicitud agregada: " + solicitud.toString());
}

public void eliminarSolicitud(Solicitud solicitud) {
    if (solicitudes.remove(solicitud)) {
        System.out.println("Solicitud eliminada: " + solicitud.toString());
    } else {
        System.out.println("La solicitud no se encontró.");
    }
}
```

### 4.3 Métodos de búsqueda/filtrado

```java
public ArrayList<Solicitud> obtenerSolicitudesCliente(int rutCliente) {
    ArrayList<Solicitud> resultado = new ArrayList<>();
    for (Solicitud r : solicitudes) {
        if (r.getCliente().getRun() == rutCliente) {
            resultado.add(r);
        }
    }
    return resultado;
}

public void obtenerSolicitudesNombre(Cliente cliente) {
    for (Solicitud r : solicitudes) {
        if (r.getCliente().getNombre().equals(cliente.getNombre())) {
            System.out.println(r.toString());
        }
    }
}
```

Nota que ambos métodos recorren la colección y usan `r.getCliente()` — es decir, aprovechan la colaboración definida en el Paso 3 para filtrar solicitudes por datos del cliente asociado.

### 4.4 Método para recorrer e imprimir toda la colección

```java
public void imprimeSolicitudes() {
    for (Solicitud r : solicitudes) {
        r.verSolicitud();
    }
}
```

---

## Paso 5: Clase `Microempresa` (main) — instanciación y diferencias

El `main` es donde todo se junta. Pero destaquemos que hay **dos bloques** bien diferenciados.

### 5.1 Instanciación de objetos individuales

Primero se crean los objetos "sueltos": `Producto`, `Cliente`, `Solicitud`. Aquí se trabaja **objeto por objeto**, llamando directamente a sus constructores y métodos.

```java
Producto p1 = new Producto(1000, 890, 1000, "Verdura", "Zanahoria", 'G');
Cliente c1 = new Cliente(111, 9111, "Francisco Isla", "fran@algo.cl", "Su casa 111", '9');

Solicitud pedido = new Solicitud();
pedido.setCliente(c1);          // se establece la colaboración
pedido.setFecha(new Date());
pedido.agregarProducto(p1, 1000); // se usa el Producto puntualmente
pedido.verSolicitud();          // se invoca el método customer de ESE objeto
```

En este bloque, cada llamada a un método (`agregarProducto`, `verSolicitud`, etc.) afecta **solo a ese objeto `Solicitud`** en particular. Si el usuario quiere ver los datos de `pedido2`, tiene que llamar explícitamente a `pedido2.verSolicitud()`.

También aparece aquí el manejo de excepción visto en el Paso 2:

```java
try {
    p0 = new Producto(100, 890, 1000, categ, "Cebolla", 'K');
} catch (IllegalArgumentException e) {
    System.out.println("ERROR: " + e.getMessage());
}
```

### 5.2 Instanciación y uso de la colección

Después de tener varios objetos `Solicitud` creados, se crea **un solo** objeto `ColeccionSolicitudes` y se le van agregando todas las solicitudes.

```java
ColeccionSolicitudes pedidos = new ColeccionSolicitudes();
pedidos.agregarSolicitud(pedido);
pedidos.agregarSolicitud(pedido2);
pedidos.agregarSolicitud(pedido3);

pedidos.obtenerSolicitudesNombre(c1);
ArrayList<Solicitud> coleccion = pedidos.obtenerSolicitudesCliente(222);
pedidos.imprimeSolicitudes();
```

### 5.3 Diferencia clave entre trabajar con la clase suelta vs. con la colección

| | Objeto individual (`Solicitud`) | Colección (`ColeccionSolicitudes`) |
|---|---|---|
| Cuántos objetos maneja cada llamada | Uno solo | Muchos a la vez (0 a N) |
| Qué necesitas para operar | Tener la referencia exacta del objeto (`pedido`, `pedido2`, etc.) | Tener la referencia de la colección; ella internamente recorre todos sus elementos |
| Ejemplo de método | `pedido.verSolicitud()` → imprime solo esa solicitud | `pedidos.imprimeSolicitudes()` → recorre el `ArrayList` e imprime todas |
| Tipo de resultado típico | Un dato o una acción sobre un objeto | Con frecuencia, otra colección filtrada (`ArrayList<Solicitud>`) o una impresión de varios objetos |
| Ejemplo de búsqueda | No aplica directamente (no hay "búsqueda" sin recorrer algo) | `pedidos.obtenerSolicitudesCliente(222)` recorre y filtra por RUT |

**💡Ideas al cierre💡:** las clases individuales (`Cliente`, `Producto`, `Solicitud`) modelan **una entidad**, mientras que la clase colección (`ColeccionSolicitudes`) modela **el conjunto** y añade comportamiento propio de manejar listas: agregar, eliminar, buscar y recorrer. El `main` demuestra el ciclo completo: primero se construyen las piezas independientes, luego las piezas que colaboran entre sí, y finalmente se agrupan en una colección que permite operar sobre todas ellas sin tener que nombrarlas una por una.

---

## Resumen del orden de aprendizaje

1. **`Cliente`** → atributos, constructores, get/set, `toString()`. Sin dependencias.
2. **`Producto`** → mismo patrón, más un setter con validación (`setSubcategoria`).
3. **`Solicitud`** → introduce la colaboración con `Cliente` (se guarda el objeto) y el uso puntual de `Producto` (se consume, no se guarda).
4. **`ColeccionSolicitudes`** → administra un `ArrayList<Solicitud>`, con métodos de agregar, eliminar, buscar y recorrer.
5. **`Microempresa` (main)** → primero instancia objetos individuales y opera sobre ellos uno a uno; luego los agrupa en la colección y opera sobre todos a la vez.
