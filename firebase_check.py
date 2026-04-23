import firebase_admin
from firebase_admin import credentials, firestore

# Configuración de credenciales
if not firebase_admin._apps:
    cred = credentials.Certificate("serviceAccountKey.json")
    firebase_admin.initialize_app(cred)

db = firestore.client()

def check_credentials():
    print("Consultando la colección 'personal' en Firebase...")
    users_ref = db.collection("personal")
    docs = users_ref.stream()

    found = False
    for doc in docs:
        found = True
        data = doc.to_dict()
        print(f"\nDocumento ID: {doc.id}")
        print(f"  - Código Personal (Usuario): {data.get('codigo_personal')}")
        print(f"  - Número Identificación (Contraseña): {data.get('numero_identificacion')}")
        print(f"  - Nombre: {data.get('nombres')} {data.get('apellidos')}")
        print(f"  - Rango: {data.get('rango_institucional')}")

    if not found:
        print("No se encontraron usuarios en la colección 'personal'.")

if __name__ == "__main__":
    check_credentials()
