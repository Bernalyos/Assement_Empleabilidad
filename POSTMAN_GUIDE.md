# Guía de Pruebas con Postman para NexTask

Esta guía te ayudará a probar la API de NexTask utilizando Postman.

## 1. Importar la Colección

1. Abre Postman.
2. Haz clic en el botón **Import**.
3. Selecciona el archivo `nex_task_postman_collection.json` que se encuentra en la raíz del proyecto.

## 2. Configuración de Variables

La colección incluye variables predefinidas:
- `base_url`: Por defecto es `http://localhost:8081`.
- `jwt_token`: Se actualiza automáticamente al hacer login.
- `project_id`: Debes copiarlo de la respuesta de "Create Project" y pegarlo en las variables de la colección o del entorno.
- `task_id`: Debes copiarlo de la respuesta de "Create Task".

## 3. Pasos para Probar

### Paso 1: Registro de Usuario
Ejecuta la petición **Auth > Register**. Esto creará un usuario de prueba (`testuser`).

### Paso 2: Login
Ejecuta la petición **Auth > Login**. 
> [!NOTE]
> La colección tiene un script que guarda automáticamente el token en la variable `jwt_token`.

### Paso 3: Crear un Proyecto
Ejecuta **Projects > Create Project**. 
- Copia el UUID que devuelve la respuesta.
- Ve a la pestaña **Variables** de la colección y pega el valor en `project_id`.

### Paso 4: Crear una Tarea
Ejecuta **Tasks > Create Task**.
- Copia el UUID que devuelve la respuesta.
- Pégalo en la variable `task_id` de la colección.

### Paso 5: Listar y Gestionar
Ahora puedes probar el resto de endpoints:
- **List Projects**: Ver todos tus proyectos.
- **Activate Project**: Activa el proyecto (recuerda que debe tener al menos una tarea).
- **Complete Task**: Marca la tarea como completada.

## 🔗 Enlaces Útiles
- **Swagger UI**: [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)
- **Frontend**: [http://localhost:8081/index.html](http://localhost:8081/index.html)
