package net.soundmining


import java.{lang => jl}

import net.soundmining.Instrument.{EnvCurve, buildFloat, buildInteger}
import net.soundmining.ModularInstrument.{AudioBus, AudioInstrument, ControlInstrument, ModularInstrument, StaticAudioBusInstrument}

object Instruments {

  def staticAudioBus(): StaticAudioBusInstrument =
    new StaticAudioBusInstrument()

  def staticControl(value: Float): StaticControl =
    new StaticControl().control(value)

  def percControl(startValue: Float, peakValue: Float, attackTime: Float, curve: Either[Float, EnvCurve]): PercControl =
    new PercControl().control(startValue, peakValue, attackTime, curve)

  def relativePercControl(startValue: Float, peakValue: Float, attackTime: Float, curve: Either[Float, EnvCurve]): PercControl =
    new RelativePercControl().control(startValue, peakValue, attackTime, curve)

  def lineControl(startValue: Float, endValue: Float): LineControl =
    new LineControl().control(startValue, endValue)

  def xlineControl(startValue: Float, endValue: Float): XLineControl =
    new XLineControl().control(startValue, endValue)

  def sineControl(freqBus: ControlInstrument, minValue: Float, maxValue: Float): SineControl =
    new SineControl().control(freqBus, minValue, maxValue)

  def controlMix(in1Bus: ControlInstrument, in2Bus: ControlInstrument): ControlMix =
    new ControlMix().mix(in1Bus, in2Bus)

  def panning(inBus: AudioInstrument, panBus: ControlInstrument): Panning =
    new Panning().pan(inBus, panBus)

  def monoDelay(inBus: AudioInstrument, delayTime: Float, decayTime: Float): MonoDelay =
    new MonoDelay().delay(inBus, delayTime, decayTime)

  def stereoDelay(inBus: AudioInstrument, delayTime: Float, decayTime: Float): StereoDelay =
    new StereoDelay().delay(inBus, delayTime, decayTime)

  def xfade(in1Bus: AudioInstrument, in2Bus: AudioInstrument, xfadeBus: ControlInstrument): XFade =
    new XFade().xfade(in1Bus: AudioInstrument, in2Bus: AudioInstrument, xfadeBus: ControlInstrument)

  def mix(in1Bus: AudioInstrument, in2Bus: AudioInstrument): Mix =
    new Mix().mix(in1Bus: AudioInstrument, in2Bus: AudioInstrument)

  def triangleOsc(ampBus: ControlInstrument, freqBus: ControlInstrument): TriangleOsc =
    new TriangleOsc().triangle(ampBus, freqBus)

  def pulseOsc(ampBus: ControlInstrument, freqBus: ControlInstrument): PulseOsc =
    new PulseOsc().pulse(ampBus, freqBus)

  def sawOsc(ampBus: ControlInstrument, freqBus: ControlInstrument): SawOsc =
    new SawOsc().saw(ampBus, freqBus)

  def sineOsc(ampBus: ControlInstrument, freqBus: ControlInstrument): SineOsc =
    new SineOsc().sine(ampBus, freqBus)

  def whiteNoise(ampBus: ControlInstrument): WhiteNoiseOsc =
    new WhiteNoiseOsc().whiteNoise(ampBus)

  def moogFilter(inBus: AudioInstrument, freqBus: ControlInstrument, gainBus: ControlInstrument): MoogFilter =
    new MoogFilter().filter(inBus, freqBus, gainBus)

  def resonantFilter(inBus: AudioInstrument, freqBus: ControlInstrument, decayBus: ControlInstrument): ResonantFilter =
    new ResonantFilter().filter(inBus, freqBus, decayBus)

  def ringModulate(carrierBus: AudioInstrument, modulatorFreqBus: ControlInstrument): RingModulate =
    new RingModulate().modulate(carrierBus, modulatorFreqBus)

  def amModulate(carrierBus: AudioInstrument, modulatorFreqBus: ControlInstrument): AmModulate =
    new AmModulate().modulate(carrierBus, modulatorFreqBus)

  def fmSineModulate(carrierFreqBus: ControlInstrument, modulatorBus: AudioInstrument,
               ampBus: ControlInstrument): FmSineModulate =
    new FmSineModulate().modulate(carrierFreqBus, modulatorBus, ampBus)

  def fmPulseModulate(carrierFreqBus: ControlInstrument, modulatorBus: AudioInstrument,
                     ampBus: ControlInstrument): FmPulseModulate =
    new FmPulseModulate().modulate(carrierFreqBus, modulatorBus, ampBus)

  def fmSawModulate(carrierFreqBus: ControlInstrument, modulatorBus: AudioInstrument,
                      ampBus: ControlInstrument): FmSawModulate =
    new FmSawModulate().modulate(carrierFreqBus, modulatorBus, ampBus)

  def fmTriangleModulate(carrierFreqBus: ControlInstrument, modulatorBus: AudioInstrument,
                    ampBus: ControlInstrument): FmTriangleModulate =
    new FmTriangleModulate().modulate(carrierFreqBus, modulatorBus, ampBus)

  class PercControl extends ControlInstrument {
    type SelfType = PercControl

    def self(): SelfType = this

    val instrumentName: String = "percControl"

    var attackTime: jl.Float = _
    var curveValue: Either[jl.Float, EnvCurve] = Left(-4f)
    var startValue: jl.Float = _
    var peakValue: jl.Float = _

    def control(startValue: Float, peakValue: Float, attackTime: Float, curve: Either[Float, EnvCurve]): SelfType = {
      this.startValue = buildFloat(startValue)
      this.peakValue = buildFloat(peakValue)
      this.attackTime = buildFloat(attackTime)
      this.curveValue = curve.left.map(buildFloat)
      self()
    }

    override def graph(parent: Seq[ModularInstrument]): Seq[ModularInstrument] =
      prependToGraph(parent)

    override def internalBuild(startTime: Float, duration: Float): Seq[Object] =
      Seq(
        "startValue", startValue,
        "peakValue", peakValue,
        "attackTime", attackTime,
        "curve", curveValue match {
          case Left(floatValue) => floatValue
          case Right(constant) => constant.name
        })
  }

  class RelativePercControl extends PercControl {
    override def internalBuild(startTime: Float, duration: Float): Seq[Object] =
      Seq(
        "startValue", startValue,
        "peakValue", peakValue,
        "attackTime", buildFloat(attackTime * duration),
        "curve", curveValue match {
          case Left(floatValue) => floatValue
          case Right(constant) => constant.name
        })
  }

  class LineControl extends ControlInstrument {
    type SelfType = LineControl

    def self(): SelfType = this

    val instrumentName: String = "lineControl"

    var startValue: jl.Float = _
    var endValue: jl.Float = _

    def control(startValue: Float, endValue: Float): SelfType = {
      this.startValue = buildFloat(startValue)
      this.endValue = buildFloat(endValue)
      self()
    }

    override def graph(parent: Seq[ModularInstrument]): Seq[ModularInstrument] =
      prependToGraph(parent)

    override def internalBuild(startTime: Float, duration: Float): Seq[Object] =
      Seq(
        "startValue", startValue,
        "endValue", endValue)
  }

  class XLineControl extends ControlInstrument {
    type SelfType = XLineControl

    def self(): SelfType = this

    val instrumentName: String = "xlineControl"

    var startValue: jl.Float = _
    var endValue: jl.Float = _

    def control(startValue: Float, endValue: Float): SelfType = {
      this.startValue = buildFloat(startValue)
      this.endValue = buildFloat(endValue)
      self()
    }

    override def graph(parent: Seq[ModularInstrument]): Seq[ModularInstrument] =
      prependToGraph(parent)

    override def internalBuild(startTime: Float, duration: Float): Seq[Object] =
      Seq(
        "startValue", startValue,
        "endValue", endValue)
  }

  class StaticControl extends ControlInstrument {
    type SelfType = StaticControl

    def self(): SelfType = this

    val instrumentName: String = "staticControl"

    var value: jl.Float = _

    def control(value: Float): SelfType = {
      this.value = buildFloat(value)
      self()
    }

    override def graph(parent: Seq[ModularInstrument]): Seq[ModularInstrument] =
      prependToGraph(parent)

    override def internalBuild(startTime: Float, duration: Float): Seq[Object] =
      Seq("value", value)
  }

  class SineControl extends ControlInstrument {
    type SelfType = SineControl

    def self(): SelfType = this

    val instrumentName: String = "sineControl"

    var freqBus: ControlInstrument = _
    var minValue: jl.Float = _
    var maxValue: jl.Float = _

    def control(freqBus: ControlInstrument, minValue: Float, maxValue: Float): SelfType = {
      this.freqBus = freqBus
      this.minValue = buildFloat(minValue)
      this.maxValue = buildFloat(maxValue)
      self()
    }

    override def graph(parent: Seq[ModularInstrument]): Seq[ModularInstrument] =
      appendToGraph(freqBus.graph(parent))

    override def internalBuild(startTime: Float, duration: Float): Seq[Object] =
      Seq(
        "freqBus", buildInteger(freqBus.getOutputBus.dynamicBus(startTime, startTime + freqBus.optionalDur.getOrElse(duration))),
        "minValue", minValue,
        "maxValue", maxValue)
  }

  class ControlMix extends ControlInstrument {
    type SelfType = ControlMix

    def self(): SelfType = this

    val instrumentName: String = "controlMix"

    var in1Bus: ControlInstrument = _
    var in2Bus: ControlInstrument = _

    def mix(in1Bus: ControlInstrument, in2Bus: ControlInstrument): SelfType = {
      this.in1Bus = in1Bus
      this.in2Bus= in2Bus
      self()
    }

    override def graph(parent: Seq[ModularInstrument]): Seq[ModularInstrument] =
      appendToGraph(in1Bus.graph(in2Bus.graph(parent)))

    override def internalBuild(startTime: Float, duration: Float): Seq[Object] = {
      val durationFallback: jl.Float = buildFloat(duration)

      Seq(
        "in1", buildInteger(
          in1Bus.getOutputBus.dynamicBus(startTime,
            startTime + in1Bus.optionalDur.getOrElse(duration))),
        "in2", buildInteger(
          in2Bus.getOutputBus.dynamicBus(startTime,
            startTime + in2Bus.optionalDur.getOrElse(duration))))
    }
  }

  class Panning extends AudioInstrument {
    type SelfType = Panning

    def self(): SelfType = this

    val instrumentName: String = "pan"

    var inBus: AudioInstrument = _
    var panBus: ControlInstrument = _

    def pan(inBus: AudioInstrument, panBus: ControlInstrument): SelfType = {
      this.inBus = inBus
      this.panBus = panBus
      self()
    }

    override def graph(parent: Seq[ModularInstrument]): Seq[ModularInstrument] =
      appendToGraph(inBus.graph(panBus.graph(parent)))

    override def internalBuild(startTime: Float, duration: Float): Seq[Object] = {
      val durationFallback: jl.Float = buildFloat(duration)

      Seq(
        "in", buildInteger(
          inBus.getOutputBus.dynamicBus(startTime,
            startTime + inBus.optionalDur.getOrElse(duration))),
        "panBus", buildInteger(
          panBus.getOutputBus.dynamicBus(startTime,
            startTime + panBus.optionalDur.getOrElse(duration))))
    }
  }

  abstract class Delay extends AudioInstrument {
    var inBus: AudioInstrument = _
    var delaytime: jl.Float = _
    var decaytime: jl.Float = _
    val nrOfChannels: Int

    def delay(inBus: AudioInstrument, delayTime: Float, decayTime: Float): SelfType = {
      this.inBus = inBus
      this.delaytime = buildFloat(delayTime)
      this.decaytime = buildFloat(decayTime)
      self()
    }

    override def graph(parent: Seq[ModularInstrument]): Seq[ModularInstrument] =
      appendToGraph(inBus.graph(parent))

    def getInputBus(startTime: Float, durationFallback: Float): Int =
      this.inBus.getOutputBus.dynamicBus(startTime,
        startTime + inBus.optionalDur.getOrElse(durationFallback))


    override def internalBuild(startTime: Float, duration: Float): Seq[Object] = {
      val durationFallback: jl.Float = buildFloat(duration)

      Seq("in", buildInteger(
          getInputBus(startTime, durationFallback)),
        "delaytime", delaytime,
        "decaytime", decaytime)
    }
  }
  class MonoDelay extends Delay {
    override type SelfType = MonoDelay

    override def self(): SelfType = this

    override val instrumentName: String = "monoDelay"

    override val nrOfChannels = 1
  }

  class StereoDelay extends Delay {
    override type SelfType = StereoDelay

    override def self(): SelfType = this

    override val instrumentName: String = "stereoDelay"

    override val nrOfChannels = 2
  }

  class XFade extends AudioInstrument {
    type SelfType = XFade

    def self(): SelfType = this

    val instrumentName: String = "xfade"

    var in1Bus: AudioInstrument = _
    var in2Bus: AudioInstrument = _
    var xfadeBus: ControlInstrument = _

    def xfade(in1Bus: AudioInstrument, in2Bus: AudioInstrument, xfadeBus: ControlInstrument): SelfType = {
      this.in1Bus = in1Bus
      this.in2Bus= in2Bus
      this.xfadeBus = xfadeBus
      self()
    }

    override def graph(parent: Seq[ModularInstrument]): Seq[ModularInstrument] =
      appendToGraph(in1Bus.graph(in2Bus.graph(xfadeBus.graph(parent))))

    override def internalBuild(startTime: Float, duration: Float): Seq[Object] = {
      val durationFallback: jl.Float = buildFloat(duration)

      Seq(
        "in1", buildInteger(
          in1Bus.getOutputBus.dynamicBus(startTime,
            startTime + in1Bus.optionalDur.getOrElse(duration))),
        "in2", buildInteger(
          in2Bus.getOutputBus.dynamicBus(startTime,
            startTime + in2Bus.optionalDur.getOrElse(duration))),
        "xfadeBus", buildInteger(
          xfadeBus.getOutputBus.dynamicBus(startTime,
            startTime + xfadeBus.optionalDur.getOrElse(duration))))
    }
  }

  class Mix extends AudioInstrument {
    type SelfType = Mix

    def self(): SelfType = this

    val instrumentName: String = "mix"

    var in1Bus: AudioInstrument = _
    var in2Bus: AudioInstrument = _

    def mix(in1Bus: AudioInstrument, in2Bus: AudioInstrument): SelfType = {
      this.in1Bus = in1Bus
      this.in2Bus= in2Bus
      self()
    }

    override def graph(parent: Seq[ModularInstrument]): Seq[ModularInstrument] =
      appendToGraph(in1Bus.graph(in2Bus.graph(parent)))

    override def internalBuild(startTime: Float, duration: Float): Seq[Object] = {
      val durationFallback: jl.Float = buildFloat(duration)

      Seq(
        "in1", buildInteger(
          in1Bus.getOutputBus.dynamicBus(startTime,
            startTime + in1Bus.optionalDur.getOrElse(duration))),
        "in2", buildInteger(
          in2Bus.getOutputBus.dynamicBus(startTime,
            startTime + in2Bus.optionalDur.getOrElse(duration))))
    }
  }

  class TriangleOsc extends AudioInstrument {
    type SelfType = TriangleOsc

    def self(): SelfType = this

    val instrumentName: String = "triangleOsc"

    var ampBus: ControlInstrument = _
    var freqBus: ControlInstrument = _

    def triangle(ampBus: ControlInstrument, freqBus: ControlInstrument): SelfType = {
      this.ampBus = ampBus
      this.freqBus = freqBus
      self()
    }

    override def graph(parent: Seq[ModularInstrument]): Seq[ModularInstrument] =
      appendToGraph(ampBus.graph(freqBus.graph(parent)))

    override def internalBuild(startTime: Float, duration: Float): Seq[Object] =
      Seq(
        "freqBus", freqBus.getOutputBus.dynamicBus(startTime,
          startTime + freqBus.optionalDur.getOrElse(duration)),
        "ampBus", ampBus.getOutputBus.dynamicBus(startTime,
          startTime + ampBus.optionalDur.getOrElse(duration)))
  }

  class PulseOsc extends AudioInstrument {
    type SelfType = PulseOsc

    def self(): SelfType = this

    val instrumentName: String = "pulseOsc"

    var ampBus: ControlInstrument = _
    var freqBus: ControlInstrument = _

    def pulse(ampBus: ControlInstrument, freqBus: ControlInstrument): SelfType = {
      this.ampBus = ampBus
      this.freqBus = freqBus
      self()
    }

    override def graph(parent: Seq[ModularInstrument]): Seq[ModularInstrument] =
      appendToGraph(ampBus.graph(freqBus.graph(parent)))

    override def internalBuild(startTime: Float, duration: Float): Seq[Object] =
      Seq(
        "freqBus", freqBus.getOutputBus.dynamicBus(startTime,
          startTime + freqBus.optionalDur.getOrElse(duration)),
        "ampBus", ampBus.getOutputBus.dynamicBus(startTime,
          startTime + ampBus.optionalDur.getOrElse(duration)))
  }

  class SawOsc extends AudioInstrument {
    type SelfType = SawOsc

    def self(): SelfType = this

    val instrumentName: String = "sawOsc"

    var ampBus: ControlInstrument = _
    var freqBus: ControlInstrument = _

    def saw(ampBus: ControlInstrument, freqBus: ControlInstrument): SelfType = {
      this.ampBus = ampBus
      this.freqBus = freqBus
      self()
    }

    override def graph(parent: Seq[ModularInstrument]): Seq[ModularInstrument] =
      appendToGraph(ampBus.graph(freqBus.graph(parent)))

    override def internalBuild(startTime: Float, duration: Float): Seq[Object] =
      Seq(
        "freqBus", freqBus.getOutputBus.dynamicBus(startTime,
          startTime + freqBus.optionalDur.getOrElse(duration)),
        "ampBus", ampBus.getOutputBus.dynamicBus(startTime,
          startTime + ampBus.optionalDur.getOrElse(duration)))
  }

  class SineOsc extends AudioInstrument {
    type SelfType = SineOsc

    def self(): SelfType = this

    val instrumentName: String = "sineOsc"

    var ampBus: ControlInstrument = _
    var freqBus: ControlInstrument = _

    def sine(ampBus: ControlInstrument, freqBus: ControlInstrument): SelfType = {
      this.ampBus = ampBus
      this.freqBus = freqBus
      self()
    }

    override def graph(parent: Seq[ModularInstrument]): Seq[ModularInstrument] =
      appendToGraph(ampBus.graph(freqBus.graph(parent)))

    override def internalBuild(startTime: Float, duration: Float): Seq[Object] =
      Seq(
        "freqBus", freqBus.getOutputBus.dynamicBus(startTime,
          startTime + freqBus.optionalDur.getOrElse(duration)),
        "ampBus", ampBus.getOutputBus.dynamicBus(startTime,
          startTime + ampBus.optionalDur.getOrElse(duration)))
  }

  class WhiteNoiseOsc extends AudioInstrument {
    type SelfType = WhiteNoiseOsc

    def self(): SelfType = this

    val instrumentName: String = "whiteNoiseOsc"

    var ampBus: ControlInstrument = _

    def whiteNoise(ampBus: ControlInstrument): SelfType = {
      this.ampBus = ampBus
      self()
    }

    override def graph(parent: Seq[ModularInstrument]): Seq[ModularInstrument] =
      appendToGraph(ampBus.graph(parent))

    override def internalBuild(startTime: Float, duration: Float): Seq[Object] =
      Seq(
        "ampBus", ampBus.getOutputBus.dynamicBus(startTime,
          startTime + ampBus.optionalDur.getOrElse(duration)))
  }

  class MoogFilter extends AudioInstrument {
    type SelfType = MoogFilter

    def self(): SelfType = this

    val instrumentName: String = "moogFilter"

    var inBus: AudioInstrument = _
    var freqBus: ControlInstrument = _
    var gainBus: ControlInstrument = _

    def filter(inBus: AudioInstrument, freqBus: ControlInstrument, gainBus: ControlInstrument): SelfType = {
      this.inBus = inBus
      this.freqBus = freqBus
      this.gainBus = gainBus
      self()
    }

    override def graph(parent: Seq[ModularInstrument]): Seq[ModularInstrument] =
      appendToGraph(inBus.graph(freqBus.graph(gainBus.graph(parent))))

    override def internalBuild(startTime: Float, duration: Float): Seq[Object] = {
      val durationFallback: jl.Float = buildFloat(duration)

      Seq(
        "in", buildInteger(
          inBus.getOutputBus.dynamicBus(startTime,
            startTime + inBus.optionalDur.getOrElse(duration))),
        "freqBus", buildInteger(
          freqBus.getOutputBus.dynamicBus(startTime,
            startTime + freqBus.optionalDur.getOrElse(duration))),
        "gainBus", buildInteger(
          gainBus.getOutputBus.dynamicBus(startTime,
            startTime + gainBus.optionalDur.getOrElse(duration))))
    }
  }

  class ResonantFilter extends AudioInstrument {
    type SelfType = ResonantFilter

    def self(): SelfType = this

    val instrumentName: String = "resonantFilter"

    var inBus: AudioInstrument = _
    var freqBus: ControlInstrument = _
    var decayBus: ControlInstrument = _

    def filter(inBus: AudioInstrument, freqBus: ControlInstrument, decayBus: ControlInstrument): SelfType = {
      this.inBus = inBus
      this.freqBus = freqBus
      this.decayBus = decayBus
      self()
    }

    override def graph(parent: Seq[ModularInstrument]): Seq[ModularInstrument] =
      appendToGraph(inBus.graph(freqBus.graph(decayBus.graph(parent))))

    override def internalBuild(startTime: Float, duration: Float): Seq[Object] = {
      val durationFallback: jl.Float = buildFloat(duration)

      Seq(
        "in", buildInteger(
          inBus.getOutputBus.dynamicBus(startTime,
            startTime + inBus.optionalDur.getOrElse(duration))),
        "freqBus", buildInteger(
          freqBus.getOutputBus.dynamicBus(startTime,
            startTime + freqBus.optionalDur.getOrElse(duration))),
        "decayBus", buildInteger(
          decayBus.getOutputBus.dynamicBus(startTime,
            startTime + decayBus.optionalDur.getOrElse(duration))))
    }
  }

  class RingModulate extends AudioInstrument {
    type SelfType = RingModulate

    def self(): SelfType = this

    val instrumentName: String = "ringModulate"

    var carrierBus: AudioInstrument = _
    var modulatorFreqBus: ControlInstrument = _

    def modulate(carrierBus: AudioInstrument, modulatorFreqBus: ControlInstrument): SelfType = {
      this.carrierBus = carrierBus
      this.modulatorFreqBus = modulatorFreqBus
      self()
    }

    override def graph(parent: Seq[ModularInstrument]): Seq[ModularInstrument] =
      appendToGraph(carrierBus.graph(modulatorFreqBus.graph(parent)))

    override def internalBuild(startTime: Float, duration: Float): Seq[Object] = {
      val durationFallback: jl.Float = buildFloat(duration)

      Seq(
        "carrierBus", buildInteger(
          carrierBus.getOutputBus.dynamicBus(startTime,
            startTime + carrierBus.optionalDur.getOrElse(duration))),
        "modulatorFreqBus", buildInteger(
          modulatorFreqBus.getOutputBus.dynamicBus(startTime,
            startTime + modulatorFreqBus.optionalDur.getOrElse(duration))))
    }
  }

  class AmModulate extends AudioInstrument {
    type SelfType = AmModulate

    def self(): SelfType = this

    val instrumentName: String = "amModulate"

    var carrierBus: AudioInstrument = _
    var modulatorFreqBus: ControlInstrument = _

    def modulate(carrierBus: AudioInstrument, modulatorFreqBus: ControlInstrument): SelfType = {
      this.carrierBus = carrierBus
      this.modulatorFreqBus = modulatorFreqBus
      self()
    }

    override def graph(parent: Seq[ModularInstrument]): Seq[ModularInstrument] =
      appendToGraph(carrierBus.graph(modulatorFreqBus.graph(parent)))

    override def internalBuild(startTime: Float, duration: Float): Seq[Object] = {
      val durationFallback: jl.Float = buildFloat(duration)

      Seq(
        "carrierBus", buildInteger(
          carrierBus.getOutputBus.dynamicBus(startTime,
            startTime + carrierBus.optionalDur.getOrElse(duration))),
        "modulatorFreqBus", buildInteger(
          modulatorFreqBus.getOutputBus.dynamicBus(startTime,
            startTime + modulatorFreqBus.optionalDur.getOrElse(duration))))
    }
  }

  abstract class FmModulate extends AudioInstrument {
    var carrierFreqBus: ControlInstrument = _
    var modulatorBus: AudioInstrument = _
    var ampBus: ControlInstrument = _

    def modulate(carrierFreqBus: ControlInstrument, modulatorBus: AudioInstrument,
                 ampBus: ControlInstrument): SelfType = {
      this.carrierFreqBus = carrierFreqBus
      this.modulatorBus = modulatorBus
      this.ampBus = ampBus
      self()
    }

    override def graph(parent: Seq[ModularInstrument]): Seq[ModularInstrument] =
      appendToGraph(carrierFreqBus.graph(modulatorBus.graph(ampBus.graph(parent))))

    override def internalBuild(startTime: Float, duration: Float): Seq[Object] = {
      val durationFallback: jl.Float = buildFloat(duration)

      Seq(
        "carrierFreqBus", buildInteger(
          carrierFreqBus.getOutputBus.dynamicBus(startTime,
            startTime + carrierFreqBus.optionalDur.getOrElse(duration))),
        "modulatorBus", buildInteger(
          modulatorBus.getOutputBus.dynamicBus(startTime,
            startTime + modulatorBus.optionalDur.getOrElse(duration))),
        "ampBus", buildInteger(
          ampBus.getOutputBus.dynamicBus(startTime,
            startTime + ampBus.optionalDur.getOrElse(duration))))
    }
  }

  class FmSineModulate extends FmModulate {
    type SelfType = FmSineModulate

    def self(): SelfType = this

    val instrumentName: String = "fmSineModulate"
  }

  class FmPulseModulate extends FmModulate {
    type SelfType = FmPulseModulate

    def self(): SelfType = this

    val instrumentName: String = "fmPulseModulate"
  }

  class FmSawModulate extends FmModulate {
    type SelfType = FmSawModulate

    def self(): SelfType = this

    val instrumentName: String = "fmSawModulate"
  }

  class FmTriangleModulate extends FmModulate {
    type SelfType = FmTriangleModulate

    def self(): SelfType = this

    val instrumentName: String = "fmTriangleModulate"
  }
}

