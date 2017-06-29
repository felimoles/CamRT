precision highp float;
uniform vec3                iResolution;
uniform sampler2D           iChannel0;
varying vec2                texCoord;

uniform float     iGlobalTime;           // shader playback time (in seconds)
uniform float     iTimeDelta;            // render time (in seconds)
uniform int       iFrame;                // shader playback frame
uniform float     iChannelTime[4];       // channel playback time (in seconds)
uniform vec3      iChannelResolution[4]; // channel resolution (in pixels)
uniform vec4      iMouse;                // mouse pixel coords. xy: current (if MLB down), zw: click
uniform sampler2D iChannel1;          // input channel. XX = 2D/Cube
uniform vec4      iDate;                 // (year, month, day, time in seconds)
uniform float     iSampleRate;


float rand(float x)
{
 return fract(sin(x) * 43758.5453);
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 uv = fragCoord.xy;

    //for uv2 (0,0) is center
    vec2 uv2;
    uv2 = uv - vec2(0.5);

    float curvature = 0.8;
    float dist = length(uv2 * vec2(1.0, 0.7)) + curvature;
    //use distance to distort coords. Distortion behaves as sqrt(x).
    uv2 *= pow(dist, 0.5) * 0.9;

    vec2 uv_vid = uv2;

    //crt start up
    float heightmul = max(1.0, 200.0 - pow(iGlobalTime, 1.5) * 200.0);
    heightmul += sin(iGlobalTime * 10.0) * max(0.0, 0.5 - pow(iGlobalTime, 4.0) * 0.2);
    uv_vid.y *= heightmul;
    float widthmul = max(1.0, 200.0 - pow(iGlobalTime, 1.5) * 500.0);
    widthmul += sin(iGlobalTime * 10.0) * max(0.0, 0.5 - pow(iGlobalTime, 4.0) * 0.9);
    uv_vid.x *= widthmul;

    //undo center offset
    uv_vid += vec2(0.5);

    //crt warmp up
    float warmup_darkness = min(1.0, iGlobalTime * 0.2);
    float warmup_darkness_tube = min(1.0, iGlobalTime * 0.7);

    vec2 uv_b = uv2 + vec2(0.5); //use distorted uv.
    uv2.x *= iResolution.z;
    uv2 += vec2(0.5);

    vec4 filter;
    float gridsize = 200.0 / 8.0;
    filter = .8 + 0.1*texture2D(iChannel0, uv2);// * vec2(1.0, 84.0/49.0) * gridsize);

    float tvborder = 0.8;
    tvborder = min(tvborder, uv_b.x * 30.0);
    tvborder = min(tvborder, uv_b.y * 30.0);
    tvborder = min(tvborder, (1.0-uv_b.x) * 30.0);
    tvborder = min(tvborder, (1.0-uv_b.y) * 30.0);
    tvborder = pow(max(tvborder, 0.0),2.0);
    tvborder *= 1.5 - pow(dist, 5.0) * 0.2;

    float signalborder = 1.0;
    signalborder = min(signalborder, uv_vid.x * 25.0);
    signalborder = min(signalborder, uv_vid.y * 25.0);
    signalborder = min(signalborder, (1.0-uv_vid.x) * 25.0);
    signalborder = min(signalborder, (1.0-uv_vid.y) * 25.0);
    signalborder = pow(max(signalborder, 0.0),2.0);
    signalborder *= 1.5 - pow(dist, 5.0) * 0.2;

    filter.rgb *= vec3(tvborder);

    //float gridsize = 200.0 / 8.0;
    //filter = texture(iChannel3, uv2 * vec2(1.0, 84.0/49.0) * gridsize);

    vec4 imp = texture2D(iChannel0, uv_vid) * signalborder;
    imp *= 0.8 + pow(sin(uv2.y * 9.0 + iGlobalTime * 3.0) * 0.5 + 0.5, 2.0) * 0.4; //traveling lines
    imp *= rand(iGlobalTime * 4.0) * 0.2 + 0.8; //flicker
    imp *= warmup_darkness;
    imp += vec4(0.15) * warmup_darkness_tube;
    imp = clamp(imp, vec4(0.0), vec4(1.0));

    fragColor = imp * filter;
}

void main() {
	mainImage(gl_FragColor, texCoord);
}