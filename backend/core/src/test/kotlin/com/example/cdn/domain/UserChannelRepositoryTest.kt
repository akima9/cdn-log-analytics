package com.example.cdn.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class UserChannelRepositoryTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val channelRepository: ChannelRepository,
    private val userChannelRepository: UserChannelRepository,
) {

    @Test
    fun `사용자-채널 권한을 저장하고 복합 PK로 조회할 수 있다`() {
        val user = userRepository.save(User(email = "user@example.com", passwordHash = "hashed"))
        val channel = channelRepository.save(Channel(name = "뉴스 채널", code = "NEWS"))
        val id = UserChannelId(userId = user.id, channelId = channel.id)
        userChannelRepository.save(UserChannel(id = id, user = user, channel = channel))

        val found = userChannelRepository.findById(id)

        assertThat(found).isPresent
        assertThat(found.get().channel.code).isEqualTo("NEWS")
    }

    @Test
    fun `사용자 기준으로 모든 채널 권한을 조회할 수 있다`() {
        val user = userRepository.save(User(email = "user@example.com", passwordHash = "hashed"))
        val ch1 = channelRepository.save(Channel(name = "뉴스 채널", code = "NEWS"))
        val ch2 = channelRepository.save(Channel(name = "스포츠 채널", code = "SPORTS"))
        userChannelRepository.save(UserChannel(id = UserChannelId(user.id, ch1.id), user = user, channel = ch1))
        userChannelRepository.save(UserChannel(id = UserChannelId(user.id, ch2.id), user = user, channel = ch2))

        val permissions = userChannelRepository.findByUser(user)

        assertThat(permissions).hasSize(2)
        assertThat(permissions.map { it.channel.code }).containsExactlyInAnyOrder("NEWS", "SPORTS")
    }
}
