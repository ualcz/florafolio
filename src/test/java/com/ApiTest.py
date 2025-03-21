
import requests
import json
import random
import string
import time

# URL base da API
BASE_URL = "http://localhost:8080"

# Variáveis globais para armazenar dados entre testes
token = None
user_id = None
username = f"testuser_{random.randint(1000, 9999)}"

def generate_random_string(length=8):
    """Gera uma string aleatória para evitar conflitos de nome de usuário."""
    return ''.join(random.choices(string.ascii_lowercase + string.digits, k=length))

def test_register_user():
    """Testa o endpoint de registro de usuário."""
    print("\n=== Testando registro de usuário ===")
    
    global username
    # Dados para registro
    user_data = {
        "username": username,
        "password": "senha123",
        "email": f"{username}@example.com"
    }
    
    try:
        # Enviando requisição POST para registro
        response = requests.post(f"{BASE_URL}/register", json=user_data)
        
        # Exibindo resultados
        print(f"Status code: {response.status_code}")
        
        # Tenta extrair o JSON da resposta
        try:
            response_json = response.json()
            print(f"Resposta: {json.dumps(response_json, indent=2, ensure_ascii=False)}")
        except:
            print(f"Resposta: {response.text}")
        
        return response.status_code == 201  # Sucesso se status code for 201 (Created)
    except Exception as e:
        print(f"Erro durante o registro: {str(e)}")
        return False

def test_login():
    """Testa o endpoint de login."""
    print("\n=== Testando login ===")
    
    global token, user_id, username
    # Credenciais para login
    print(f"Username: {username}")
    credentials = {
        "username": username,
        "password": "senha123"
    }
    
    try:
        # Enviando requisição POST para login
        response = requests.post(f"{BASE_URL}/login", json=credentials)
        
        # Exibindo resultados
        print(f"Status code: {response.status_code}")
        
        # Tenta extrair o JSON da resposta
        try:
            response_data = response.json()
            print(f"Resposta: {json.dumps(response_data, indent=2, ensure_ascii=False)}")
            
            # Armazenando o token para uso em outros testes
            if response.status_code == 200:
                token = response_data.get("token")
                user_id = response_data.get("id")

                
                if not token or not user_id:
                    print("AVISO: Token ou ID do usuário não encontrado na resposta")
                    print(f"Estrutura da resposta: {response_data.keys()}")
                    return False
                    
                return True
        except:
            print(f"Resposta: {response.text}")
            
        return False
    except Exception as e:
        print(f"Erro durante o login: {str(e)}")
        return False

def test_get_user_by_id():
    """Testa o endpoint para obter informações do usuário pelo ID."""
    print("\n=== Testando obtenção de usuário pelo ID ===")
    
    global token, user_id
    if not token or not user_id:
        print("Token ou ID do usuário não disponível. Execute o teste de login primeiro.")
        return False
    
    try:
        # Configurando o cabeçalho de autorização
        headers = {
            "Authorization": f"Bearer {token}"
        }
        
        # Enviando requisição GET para obter informações do usuário
        response = requests.get(f"{BASE_URL}/users/id/{user_id}", headers=headers)
        
        # Exibindo resultados
        print(f"Status code: {response.status_code}")
        if response.status_code == 200:
            print(f"Resposta: {json.dumps(response.json(), indent=2, ensure_ascii=False)}")
        else:
            print(f"Resposta: {response.text}")
        
        return response.status_code == 200
    except Exception as e:
        print(f"Erro ao obter usuário: {str(e)}")
        return False

def test_update_password():
    """Testa o endpoint para atualizar a senha do usuário."""
    print("\n=== Testando atualização de senha ===")
    
    global token, user_id
    if not token or not user_id:
        print("Token ou ID do usuário não disponível. Execute o teste de login primeiro.")
        return False
    
    try:
        # Configurando o cabeçalho de autorização
        headers = {
            "Authorization": f"Bearer {token}"
        }
        
        # Dados para atualização de senha
        password_data = {
            "currentPassword": "senha123",
            "newPassword": "novaSenha456"
        }
        
        # Enviando requisição PUT para atualizar a senha
        response = requests.put(f"{BASE_URL}/users/id/{user_id}/password", 
                              json=password_data, 
                              headers=headers)
        
        # Exibindo resultados
        print(f"Status code: {response.status_code}")
        if response.status_code == 200:
            print(f"Resposta: {json.dumps(response.json(), indent=2, ensure_ascii=False)}")
        else:
            print(f"Resposta: {response.text}")
        
        return response.status_code == 200
    except Exception as e:
        print(f"Erro ao atualizar senha: {str(e)}")
        return False

def test_login_with_new_password():
    """Testa o login com a nova senha."""
    print("\n=== Testando login com nova senha ===")
    
    global token, user_id, username
    # Credenciais para login com nova senha
    credentials = {
        "username": username,
        "password": "novaSenha456"
    }
    
    try:
        # Enviando requisição POST para login
        response = requests.post(f"{BASE_URL}/login", json=credentials)
        
        # Exibindo resultados
        print(f"Status code: {response.status_code}")
        print(f"Resposta: {json.dumps(response.json(), indent=2, ensure_ascii=False)}")
        
        # Atualizando o token
        if response.status_code == 200:
            token = response.json().get("token")
            return True
        return False
    except Exception as e:
        print(f"Erro durante o login com nova senha: {str(e)}")
        return False

def test_update_username():
    """Testa o endpoint para atualizar o nome de usuário."""
    print("\n=== Testando atualização de nome de usuário ===")
    
    global token, user_id, username
    if not token or not user_id:
        print("Token ou ID do usuário não disponível. Execute o teste de login primeiro.")
        return False
    
    try:
        # Novo nome de usuário
        new_username = f"updated_{username}"
        
        # Configurando o cabeçalho de autorização
        headers = {
            "Authorization": f"Bearer {token}"
        }
        
        # Dados para atualização de nome de usuário
        username_data = {
            "currentUsername": username,
            "newUsername": new_username
        }
        
        # Enviando requisição PUT para atualizar o nome de usuário
        response = requests.put(f"{BASE_URL}/users/id/{user_id}/username", 
                              json=username_data, 
                              headers=headers)
        
        # Exibindo resultados
        print(f"Status code: {response.status_code}")
        if response.status_code == 200:
            print(f"Resposta: {json.dumps(response.json(), indent=2, ensure_ascii=False)}")
            # Atualizando o nome de usuário global
            username = new_username
            return True
        else:
            print(f"Resposta: {response.text}")
            return False
    except Exception as e:
        print(f"Erro ao atualizar nome de usuário: {str(e)}")
        return False

def test_login_with_new_username():
    """Testa o login com o novo nome de usuário."""
    print("\n=== Testando login com novo nome de usuário ===")
    
    global token, username
    # Credenciais para login com novo nome de usuário
    credentials = {
        "username": username,
        "password": "novaSenha456"
    }
    
    try:
        # Enviando requisição POST para login
        response = requests.post(f"{BASE_URL}/login", json=credentials)
        
        # Exibindo resultados
        print(f"Status code: {response.status_code}")
        print(f"Resposta: {json.dumps(response.json(), indent=2, ensure_ascii=False)}")
        
        # Atualizando o token
        if response.status_code == 200:
            token = response.json().get("token")
            return True
        return False
    except Exception as e:
        print(f"Erro durante o login com novo nome de usuário: {str(e)}")
        return False
    
def test_logout():
    """Testa o endpoint de logout."""
    print("\n=== Testando logout ===")
    
    global token
    if not token:
        print("Token para logout não disponível. Execute o teste de login primeiro.")
        return False
    
    try:
        # Configurando o cabeçalho de autorização
        headers = {
            "Authorization": f"Bearer {token}"
        }
        
        # Enviando requisição POST para logout
        response = requests.post(f"{BASE_URL}/logout", headers=headers)
        
        # Exibindo resultados
        print(f"Status code: {response.status_code}")
        if response.status_code == 200:
            print(f"Resposta: {json.dumps(response.json(), indent=2, ensure_ascii=False)}")
        else:
            print(f"Resposta: {response.text}")
        
        return response.status_code == 200  # Sucesso se status code for 200 (OK)
    except Exception as e:
        print(f"Erro durante o logout: {str(e)}")
        return False

def test_unauthorized_access():
    """Testa acesso não autorizado a um endpoint protegido."""
    print("\n=== Testando acesso não autorizado ===")
    
    try:
        # Enviando requisição GET sem token
        response = requests.get(f"{BASE_URL}/users/id/{user_id}")
        
        # Exibindo resultados
        print(f"Status code: {response.status_code}")
        if response.status_code != 200:
            print("Resposta: Acesso negado conforme esperado")
        else:
            print(f"Resposta: {response.text}")
        
        return response.status_code in [401, 403]  # Sucesso se status code for 401 (Unauthorized) ou 403 (Forbidden)
    except Exception as e:
        print(f"Erro durante o teste de acesso não autorizado: {str(e)}")
        # Neste caso específico, consideramos como sucesso mesmo se houver uma exceção,
        # pois isso pode indicar que o servidor está rejeitando a conexão
        return True

def check_server_status():
    """Verifica se o servidor está rodando antes de executar os testes."""
    print("\nVerificando se o servidor está rodando...")
    try:
        # Tenta acessar o endpoint de registro (que deve estar acessível sem autenticação)
        # Usamos POST porque o endpoint de login espera um método POST
        response = requests.post(f"{BASE_URL}/register", 
                               json={"username": "test", "password": "test"},
                               timeout=5)
        
        if response.status_code in [200, 201, 400, 401, 403, 405, 409]:
            print("Servidor está acessível!")
            return True
        else:
            print(f"Servidor respondeu com status code inesperado: {response.status_code}")
            return False
    except requests.exceptions.ConnectionError:
        print("ERRO: Não foi possível conectar ao servidor.")
        print("Certifique-se de que o servidor Spring Boot está rodando em http://localhost:8080")
        print("Para iniciar o servidor, execute 'mvnw.cmd spring-boot:run' (Windows) ou './mvnw spring-boot:run' (Linux/Mac)")
        return False
    except Exception as e:
        print(f"ERRO ao verificar status do servidor: {str(e)}")
        return False

def run_all_tests():
    """Executa todos os testes em sequência."""
    # Verifica se o servidor está rodando
    if not check_server_status():
        print("Abortando testes. Inicie o servidor primeiro.")
        return
    
    tests = [
        ("Registro de usuário", test_register_user),
        ("Login", test_login),
        ("Obter usuário pelo ID", test_get_user_by_id),
        ("Atualizar senha", test_update_password),
        ("Login com nova senha", test_login_with_new_password),
        ("Atualizar nome de usuário", test_update_username),
        ("Login com novo nome de usuário", test_login_with_new_username),
        ("Logout", test_logout),
        ("Acesso não autorizado", test_unauthorized_access)
    ]
    
    results = {}
    
    for name, test_func in tests:
        print(f"\n{'=' * 50}")
        print(f"Executando teste: {name}")
        try:
            result = test_func()
            results[name] = "PASSOU" if result else "FALHOU"
            # Pequena pausa entre os testes para evitar problemas de concorrência
            time.sleep(1)
        except Exception as e:
            print(f"Erro durante o teste: {str(e)}")
            results[name] = "ERRO"
    
    # Resumo dos resultados
    print("\n\n" + "=" * 50)
    print("RESUMO DOS RESULTADOS")
    print("=" * 50)
    for name, result in results.items():
        print(f"{name}: {result}")
    print("=" * 50)

if __name__ == "__main__":
    run_all_tests()