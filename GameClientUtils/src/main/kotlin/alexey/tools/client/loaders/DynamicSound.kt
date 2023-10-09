package alexey.tools.client.loaders

import alexey.tools.common.context.ImmutableVariable
import com.badlogic.gdx.audio.Sound

class DynamicSound(private val sound: Sound,
                   private val volume: ImmutableVariable): Sound {

    override fun dispose() = sound.dispose()
    override fun play() = sound.play(volume.toFloat())
    override fun play(volume: Float) = sound.play(volume * this.volume.toFloat())
    override fun play(volume: Float, pitch: Float, pan: Float) = sound.play(volume * this.volume.toFloat(), pitch, pan)
    override fun loop() = sound.loop(volume.toFloat())
    override fun loop(volume: Float) = sound.loop(volume * this.volume.toFloat())
    override fun loop(volume: Float, pitch: Float, pan: Float) = sound.loop(volume * this.volume.toFloat(), pitch, pan)
    override fun stop() = sound.stop()
    override fun stop(soundId: Long) = sound.stop(soundId)
    override fun pause() = sound.pause()
    override fun pause(soundId: Long) = sound.pause(soundId)
    override fun resume() = sound.resume()
    override fun resume(soundId: Long) = sound.resume(soundId)
    override fun setLooping(soundId: Long, looping: Boolean) = sound.setLooping(soundId, looping)
    override fun setPitch(soundId: Long, pitch: Float) = sound.setPitch(soundId, pitch)
    override fun setVolume(soundId: Long, volume: Float) = sound.setVolume(soundId, volume * this.volume.toFloat())
    override fun setPan(soundId: Long, pan: Float, volume: Float) = sound.setPan(soundId, pan, volume)
}