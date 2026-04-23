import firebase_admin
from firebase_admin import credentials, firestore

# Configuración de credenciales
cred = credentials.Certificate("serviceAccountKey.json")
firebase_admin.initialize_app(cred)
db = firestore.client()

# Datos de ejemplo (placeholders) para todas las tablas
data = {
    "acompanante": {
        "0": {"nombres": "Placeholder", "apellidos": "Acompañante", "telefono": "00000000"}
    },
    "destino_traslado": {
        "1": {"nombre": "Hospital Zacapa"},
        "2": {"nombre": "IGSS"},
        "3": {"nombre": "Centro de Salud Zacapa"},
        "4": {"nombre": "Centro de Salud Teculután"}
    },
    "direccion_emergencia": {
        "0": {"ubicacion": "Placeholder Ubicación", "referencias": "Ninguna", "observaciones": "Ninguna"}
    },
    "estado_emergencia": {
        "1": {"nombre": "Pendiente"},
        "2": {"nombre": "Finalizada"}
    },
    "estado_paciente": {
        "1": {"nombre": "Estable"},
        "2": {"nombre": "Crítico"},
        "3": {"nombre": "Fallecido"}
    },
    "estado_unidad": {
        "1": {"nombre": "Disponible"},
        "2": {"nombre": "En servicio"},
        "3": {"nombre": "Mantenimiento"}
    },
    "prioridad": {
        "1": {"nombre": "Baja"},
        "2": {"nombre": "Media"},
        "3": {"nombre": "Alta"},
        "4": {"nombre": "Crítica"}
    },
    "tipo_personal": {
        "1": {"nombre": "Piloto"},
        "2": {"nombre": "Paramédico"},
        "3": {"nombre": "Bombero"}
    },
    "tipo_servicio": {
        "1": {"nombre": "Accidente de Tránsito"},
        "2": {"nombre": "Incendio"},
        "3": {"nombre": "Enfermedad Común"},
        "4": {"nombre": "Parto"}
    },
    "tipo_unidad": {
        "1": {"nombre": "Ambulancia"},
        "2": {"nombre": "Motobomba"},
        "3": {"nombre": "Rescate"}
    },
    "personal": {
        "0": {
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
        }
    },
    "unidad": {
        "0": {
            "id_tipo_unidad": 1,
            "placa": "000AAA",
            "marca": "Toyota",
            "modelo": "Hiace",
            "fecha_registro": "2024-01-01",
            "color": "Rojo/Blanco",
            "id_estado_unidad": 1
        }
    },
    "paciente": {
        "0": {
            "nombres": "Placeholder",
            "apellidos": "Paciente",
            "domicilio": "Desconocido",
            "edad": 0,
            "sexo": "M",
            "id_estado_paciente": 1
        }
    },
    "traslado": {
        "0": {
            "direccion_origen": "Escena",
            "id_destino": 1,
            "hora_llegada": "2024-01-01 00:00:00"
        }
    },
    "emergencia": {
        "0": {
            "fecha_hora_llamada": "2024-01-01 00:00:00",
            "telefono_solicitante": "00000000",
            "nombres_solicitante": "Sistema",
            "apellidos_solicitante": "Placeholder",
            "id_tipo_servicio": 1,
            "id_prioridad": 1,
            "id_estado_emergencia": 1,
            "id_direccion": 0,
            "numero_unidad": 0,
            "id_paciente": 0,
            "id_acompanante": 0,
            "id_traslado": 0,
            "campo": "Prueba de sistema"
        }
    }
}

def seed_data():
    for collection_name, documents in data.items():
        print(f"Poblando colección: {collection_name}...")
        for doc_id, doc_data in documents.items():
            db.collection(collection_name).document(doc_id).set(doc_data)
    print("¡Firebase poblado con éxito!")

if __name__ == "__main__":
    seed_data()
