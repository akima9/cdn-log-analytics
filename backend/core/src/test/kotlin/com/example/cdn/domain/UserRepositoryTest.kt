package com.example.cdn.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class UserRepositoryTest @Autowired constructor(
    private val userRepository: UserRepository,
) {

    @Test
    fun `사용자를 저장하고 이메일로 조회할 수 있다`() {
        userRepository.save(User(email = "user@example.com", passwordHash = "hashed"))

        val found = userRepository.findByEmail("user@example.com")

        assertThat(found).isNotNull
        assertThat(found!!.email).isEqualTo("user@example.com")
    }

    @Test
    fun `role 기본값은 USER이다`() {
        val user = userRepository.save(User(email = "user@example.com", passwordHash = "hashed"))

        assertThat(user.role).isEqualTo(UserRole.USER)
    }
}
