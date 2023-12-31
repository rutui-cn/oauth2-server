package cn.rutui.oauth2server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 该配置类，主要处理用户名和密码的校验等事宜
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	/**
	 * 注册一个认证管理器对象到容器
	 */
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}


	/**
	 * 密码编码对象（密码不进行加密处理）
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}

	/**
	 * 处理用户名和密码验证事宜
	 * 1）客户端传递username和password参数到认证服务器
	 * 2）一般来说，username和password会存储在数据库中的用户表中
	 * 3）根据用户表中数据，验证当前传递过来的用户信息的合法性
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// 在这个方法中就可以去关联数据库了，当前我们先把用户信息配置在内存中
		// 实例化一个用户对象(相当于数据表中的一条用户记录)
		//通过内存创建用户名和密码
		auth.inMemoryAuthentication().withUser("user").password("123456").roles("user");
		auth.inMemoryAuthentication().withUser("admin").password("123456").roles("admin");
	}

}