USE ai_chatbot;

-- 插入初始用户admin，密码123456（MD5加密）
INSERT INTO users (username, password, create_time) VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', NOW());