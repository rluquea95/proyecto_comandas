# ReadyTapas 🗒️

### Emulador de Android

### Estos pasos son comunes en ambas opciones, ya que necesitamos tener configurado un emulador con Android.

#### 1. Ejecutamos Android Studio y creamos un emulador con las siguientes especificaciones:
<img src="https://github.com/user-attachments/assets/9a95a490-a26e-4925-97d3-bde80eda3e1c" width="500" /> <br/>
<img src="https://github.com/user-attachments/assets/653ce1e7-9be6-4b10-92c6-810a9c1fed20" width="500" /> <br/>
<img src="https://github.com/user-attachments/assets/a7bc8c5f-7f3c-42a9-80ca-0bb4d26d49a3" width="500" /> 

<br/>

#### 2. Ejecutamos el emulador, dejamos que cargue toda la configuración verificamos que el dispositivo <br/> tiene conexión a Internet y actualizamos todos los servicios desde Google Play, así nos aseguramos <br/> de que todos los servicios de Google están actualizados y no hay problema.
<img src="https://github.com/user-attachments/assets/f88d565d-86c3-4bb9-a252-bebd3e88a691" width="400" />

<br/>
<br/>

-----

## 🅰️ Clonación del proyecto y replicación del mismo

## :one: Registrarse en Firebase Firestore y crear un proyecto

### Opción 1: Podemos clonar el [repositorio](https://github.com/rluquea95/proyecto_comandas) desde GitHub 

<img src="https://github.com/user-attachments/assets/25da5171-2d5c-4edd-80b1-3d1ee03bd449" width="500" />

<br/>
<br/>

### Opción 2: Importar el proyecto enviado en el zip
<br/>
<br/>

## :two: Registrarse en Firebase Firestore y crear un proyecto

<img src="https://github.com/user-attachments/assets/6c0f7fb6-970f-4378-a8d3-d8f573c886ee" width="500" />
<br/>
<br/>

## :three: Asegurarnos de que las reglas por primera vez permiten el acceso sin autenticación <br> (de forma predeterminada, Google lo configura así durante un periodo de 30 días)
![Captura de pantalla 2025-05-25 164545](https://github.com/user-attachments/assets/43fa7857-b891-4002-8474-eaf39f5872eb)


## :four: Registrar la aplicación para usarla con Firebase
![image](https://github.com/user-attachments/assets/8bc49cf0-a38e-402d-8d04-d2009d663168)
<br/>
<br/>

## :five: Descargamos el json y lo arrastramos hasta la ruta app
![image](https://github.com/user-attachments/assets/77f035da-91e9-40a8-ad90-5eb45979d02c)
<br/>
<br/>

## :six: Verificamos tener todas las dependencias en build.gradle.kts (project y app) <br/> En principio todas las dependencias ya están en el proyecto, pero recomiendo verificar que se disponen de todas
![image](https://github.com/user-attachments/assets/274861aa-a4c2-4204-b99d-218fd72d8f6d)
<br/>
<br/>

## :seven: Verificar que el proyecto se actualiza correctamente y todas las dependencias se añaden sin problema y carga el archivo JSON
<br/>
<br/>

## 8️⃣ En este caso, la carga inicial de las colecciones Carta y Producto, puede que de <br/> conflictos ya que el proyecto cuenta con más clases y código que cuando se implementó la carga, por lo tanto, si hay algun conflicto recomiendo comentar las clases implicadas.<br/>
### La clase Main se debe comentar (o copiar el codigo aparte) y sustituirlo por esto:
```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            //Aquí se instancia FirestoreUploader y se pasa context para poder acceder al JSON que contiene Mesas
            val firestoreUploaderMesa = FirestoreUploaderMesa(this)

            //Llamamos a la función uploadJsonData para subir los datos a Firestore
            firestoreUploaderMesa.uploadJsonDataMesa()
        setContent {

                //Aquí se instancia FirestoreUploader y se pasa context para poder acceder al JSON que contiene Productos
                val firestoreUploaderProducto = FirestoreUploaderProducto(this)

                //Llamamos a la función uploadJsonData para subir los datos a Firestore
                firestoreUploaderProducto.uploadJsonDataProducto()
       }
    }
}
```
<br/>
<br/>

## :nine: Una vez, se realiza la carga inicial, habilitamos Firestore Authentication mediante correo electrónico y vamos añadiendo los usuarios que queremos que puedan <br/> interactuar en la base de datos.
![image](https://github.com/user-attachments/assets/11c1455d-2e31-4219-83b1-008e037a806b)

<br/>
<br/>

## :one::zero: Por último cambiamos las reglas de Firestore, para que solo permita el acceso a usuarios autentificados
![Captura de pantalla 2025-05-25 165405](https://github.com/user-attachments/assets/3a67741b-0f49-4e6a-a54f-0e8f622c81fa)

<br/>
<br/>

## :one::one: Modificamos la clase MainActivity para que vuelva a tener el código original y que la aplicación funcione con normalidad (en caso de haber comentado alguna clase por problemas para hacer la carga inicial de las colecciones, las volvemos a descomentar)
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReadyTapasTheme {

                val navController = rememberNavController()

                // Función de logout que cierra la sesión utilizando AuthRepository
                val onLogoutClick: () -> Unit = {
                    authRepository.logout()
                }

                AppNavHost(
                    navController = navController,
                    authRepository = authRepository,
                    onLogoutClick = onLogoutClick // Pasamos la función de logout a AppNavHost
                )
            }
        }
    }
}
```

<br/>
<br/>

## :one::two: Comprobamos que el proyecto tiene todas las dependencias instaladas y ejecutamos la aplicación en el emulador. Simplemente abrimos la aplicación ReadyTapas e iniciamos sesión con el usuario que se haya registrado

-----
<br/>
<br/>

## 🅱️ Ejecución del apk

#### 3. Una vez el emulador está operativo, simplemente arrastramos el archivo apk.debug
<img src="https://github.com/user-attachments/assets/8caefea5-2339-439c-b88a-0422f332a237" width="200" />
<br/>
<br/>

#### 4. Buscamos la aplicación en el cajón de aplicaciones, la abrimos e introducimos el usuario y <br/> contraseña que permite el acceso a la aplicación (leer archivo credenciales que se incluye en el zip de la entrega)
<img src="https://github.com/user-attachments/assets/3b13e00c-be74-4411-95ec-ef4a76f08dcb" width="300" />
