// 全局错误处理，处理401/403错误
(function() {
    // 拦截fetch请求的响应
    const originalFetch = window.fetch;
    window.fetch = function() {
        return originalFetch.apply(this, arguments)
            .then(response => {
                if (response.status === 401 || response.status === 403) {
                    // 显示登录弹窗
                    showLoginModal();
                    return Promise.reject(new Error('未登录或权限不足'));
                }
                return response;
            });
    };

    // 拦截XMLHttpRequest请求的响应
    const originalXhrOpen = XMLHttpRequest.prototype.open;
    XMLHttpRequest.prototype.open = function() {
        this.addEventListener('readystatechange', function() {
            if (this.readyState === 4) {
                if (this.status === 401 || this.status === 403) {
                    // 显示登录弹窗
                    showLoginModal();
                }
            }
        });
        originalXhrOpen.apply(this, arguments);
    };

    // 显示登录弹窗
    function showLoginModal() {
        // 检查是否已经存在登录弹窗
        if (document.getElementById('loginModal')) {
            return;
        }

        // 创建登录弹窗
        const modal = document.createElement('div');
        modal.id = 'loginModal';
        modal.style.position = 'fixed';
        modal.style.top = '0';
        modal.style.left = '0';
        modal.style.width = '100%';
        modal.style.height = '100%';
        modal.style.backgroundColor = 'rgba(0, 0, 0, 0.6)';
        modal.style.display = 'flex';
        modal.style.justifyContent = 'center';
        modal.style.alignItems = 'center';
        modal.style.zIndex = '10000';
        modal.style.animation = 'fadeIn 0.3s ease forwards';

        // 弹窗内容
        modal.innerHTML = `
            <div style="background-color: white; padding: 40px; border-radius: 20px; box-shadow: 0 25px 60px rgba(0,0,0,0.3); width: 420px; max-width: 90vw; position: relative; animation: slideUp 0.4s ease forwards;">
                <div style="position: absolute; top: 15px; right: 15px; cursor: pointer; font-size: 24px; color: #a0aec0; transition: color 0.3s ease;" id="closeModal">&times;</div>
                <h2 style="text-align: center; margin-bottom: 8px; color: #1a202c; font-size: 24px; font-weight: 700;">请登录</h2>
                <p style="text-align: center; margin-bottom: 30px; color: #a0aec0; font-size: 14px;">登录后继续访问</p>
                <div style="display: none; background: #fff5f5; border: 1px solid #fed7d7; color: #c53030; padding: 12px 16px; border-radius: 10px; font-size: 13px; margin-bottom: 20px; text-align: center;" id="modalErrorMsg"></div>
                <form id="modalLoginForm">
                    <div style="margin-bottom: 20px; position: relative;">
                        <label style="display: block; margin-bottom: 8px; font-size: 13px; font-weight: 600; color: #4a5568;">用户名</label>
                        <div style="position: relative;">
                            <input type="text" id="modalUsername" style="width: 100%; padding: 14px 16px 14px 48px; border: 2px solid #e2e8f0; border-radius: 12px; font-size: 15px; color: #2d3748; outline: none; transition: all 0.3s ease; background: #f7fafc; font-family: inherit;" required>
                            <span style="position: absolute; left: 16px; top: 50%; transform: translateY(-50%); font-size: 16px; color: #a0aec0; pointer-events: none; transition: color 0.3s;" class="input-icon">👤</span>
                        </div>
                    </div>
                    <div style="margin-bottom: 20px; position: relative;">
                        <label style="display: block; margin-bottom: 8px; font-size: 13px; font-weight: 600; color: #4a5568;">密码</label>
                        <div style="position: relative;">
                            <input type="password" id="modalPassword" style="width: 100%; padding: 14px 16px 14px 48px; border: 2px solid #e2e8f0; border-radius: 12px; font-size: 15px; color: #2d3748; outline: none; transition: all 0.3s ease; background: #f7fafc; font-family: inherit;" required>
                            <span style="position: absolute; left: 16px; top: 50%; transform: translateY(-50%); font-size: 16px; color: #a0aec0; pointer-events: none; transition: color 0.3s;" class="input-icon">🔒</span>
                        </div>
                    </div>
                    <button type="submit" style="width: 100%; padding: 16px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; border-radius: 12px; font-size: 16px; font-weight: 600; cursor: pointer; transition: all 0.3s ease; letter-spacing: 0.5px; font-family: inherit; position: relative; overflow: hidden;" id="modalLoginBtn">
                        <span id="modalBtnText">登录</span>
                    </button>
                    <div style="text-align: center; margin-top: 20px; font-size: 14px; color: #a0aec0;">
                        还没有账号？ <a href="/register" style="color: #667eea; text-decoration: none; font-weight: 600; transition: color 0.3s; position: relative;">立即注册</a>
                    </div>
                </form>
            </div>
        `;

        // 添加动画样式
        const style = document.createElement('style');
        style.textContent = `
            @keyframes fadeIn {
                from { opacity: 0; }
                to { opacity: 1; }
            }
            @keyframes slideUp {
                from { transform: translateY(30px); opacity: 0; }
                to { transform: translateY(0); opacity: 1; }
            }
            @keyframes fadeOut {
                from { opacity: 1; }
                to { opacity: 0; }
            }
            @keyframes shake {
                0%, 100% { transform: translateX(0); }
                25% { transform: translateX(-8px); }
                75% { transform: translateX(8px); }
            }
            @keyframes spin {
                to { transform: rotate(360deg); }
            }
            .loading-spinner {
                display: inline-block;
                width: 20px;
                height: 20px;
                border: 2px solid rgba(255,255,255,0.3);
                border-radius: 50%;
                border-top-color: #fff;
                animation: spin 1s ease-in-out infinite;
                margin-right: 10px;
            }
            .error-msg.show {
                display: block !important;
                animation: shake 0.4s ease, fadeIn 0.3s ease;
            }
        `;
        document.head.appendChild(style);

        // 添加到页面
        document.body.appendChild(modal);

        // 关闭弹窗
        document.getElementById('closeModal').addEventListener('click', function() {
            modal.style.animation = 'fadeOut 0.3s ease forwards';
            setTimeout(() => {
                document.body.removeChild(modal);
                document.head.removeChild(style);
            }, 300);
        });

        // 输入框焦点效果
        const inputs = modal.querySelectorAll('input');
        inputs.forEach(input => {
            input.addEventListener('focus', function() {
                this.style.borderColor = '#667eea';
                this.style.background = '#fff';
                this.style.boxShadow = '0 0 0 4px rgba(102, 126, 234, 0.15)';
                this.style.transform = 'translateY(-2px)';
                const icon = this.nextElementSibling;
                if (icon) {
                    icon.style.color = '#667eea';
                    icon.style.transform = 'translateY(-50%) scale(1.1)';
                }
            });
            input.addEventListener('blur', function() {
                this.style.borderColor = '#e2e8f0';
                this.style.background = '#f7fafc';
                this.style.boxShadow = 'none';
                this.style.transform = 'translateY(0)';
                const icon = this.nextElementSibling;
                if (icon) {
                    icon.style.color = '#a0aec0';
                    icon.style.transform = 'translateY(-50%) scale(1)';
                }
            });
        });

        // 登录按钮悬停效果
        const loginBtn = document.getElementById('modalLoginBtn');
        loginBtn.addEventListener('mouseover', function() {
            this.style.transform = 'translateY(-2px)';
            this.style.boxShadow = '0 8px 25px rgba(102, 126, 234, 0.4)';
        });
        loginBtn.addEventListener('mouseout', function() {
            this.style.transform = 'translateY(0)';
            this.style.boxShadow = 'none';
        });

        // 注册链接悬停效果
        const registerLink = modal.querySelector('a');
        registerLink.addEventListener('mouseover', function() {
            this.style.color = '#764ba2';
        });
        registerLink.addEventListener('mouseout', function() {
            this.style.color = '#667eea';
        });

        // 显示错误信息
        function showModalError(msg) {
            const el = document.getElementById('modalErrorMsg');
            el.textContent = msg;
            el.classList.add('show');
            setTimeout(() => el.classList.remove('show'), 4000);
        }

        // 登录表单提交
        document.getElementById('modalLoginForm').addEventListener('submit', function(e) {
            e.preventDefault();
            
            const username = document.getElementById('modalUsername').value.trim();
            const password = document.getElementById('modalPassword').value;
            const btn = document.getElementById('modalLoginBtn');
            const btnText = document.getElementById('modalBtnText');
            
            if (!username || !password) {
                showModalError('请填写所有字段');
                return;
            }
            
            btn.disabled = true;
            btnText.innerHTML = '<span class="loading-spinner"></span>登录中...';
            
            const data = {
                username: username,
                password: password
            };
            
            fetch('/api/public/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })
            .then(res => res.json())
            .then(data => {
                btn.disabled = false;
                btnText.textContent = '登录';
                if (data.code === 200) {
                    if (data.data && data.data.token) {
                        localStorage.setItem('token', data.data.token);
                        // 存储完整用户信息（含vipExpireDate），避免VIP状态切换页面后丢失
                        const userData = {
                            username: data.data.username,
                            email: data.data.email,
                            role: data.data.role,
                            userId: data.data.userId,
                            vipExpireDate: data.data.vipExpireDate || null,
                            avatar: data.data.avatar || null
                        };
                        localStorage.setItem('user', JSON.stringify(userData));
                    }
                    modal.style.animation = 'fadeOut 0.3s ease forwards';
                    setTimeout(() => {
                        document.body.removeChild(modal);
                        document.head.removeChild(style);
                        // 刷新页面
                        window.location.reload();
                    }, 300);
                } else {
                    showModalError(data.message || '登录失败，请检查用户名和密码');
                }
            })
            .catch(err => {
                btn.disabled = false;
                btnText.textContent = '登录';
                showModalError('网络错误，请稍后重试');
            });
        });
    }
})();