import firebase_admin
from firebase_admin import credentials, firestore

# Configuración de credenciales
if not firebase_admin._apps:
    cred = credentials.Certificate("serviceAccountKey.json")
    firebase_admin.initialize_app(cred)

db = firestore.client()

def fix_data():
    print("Corrigiendo el usuario administrador en Firebase...")
    # Forzar que el código sea 0 y que esté presente en el documento
    doc_ref = db.collection("personal").document("0")
    doc_ref.set({
        "codigo_personal": 0,
        "nombres": "Admin",
        "apellidos": "General",
        "telefono": "00000000",
        "direccion": "Estación Central",
        "fecha_nacimiento": "2000-01-01",
        "fecha_ingreso": "2024-01-01",
        "sexo": "M",
        "rango_institucional": "Oficial",
        "id_tipo_personal": 3,
        "estado_personal": "Activo",
        "numero_identificacion": "admin"
    })
    print("¡Usuario administrador corregido! Usuario: 0, Contraseña: admin")

if __name__ == "__main__":
    fix_data()
