/**
 * Award Vote Lanka — small UI polish script.
 * - Scroll-reveal: elements with class "reveal-on-scroll" fade/rise into view
 *   the first time they cross into the viewport (IntersectionObserver).
 * - Navbar elevation: adds a subtle "scrolled" shadow state once the page
 *   has scrolled past the hero, so the dark navbar reads as elevated chrome.
 * Degrades silently if IntersectionObserver isn't available - no errors.
 */
(function () {
    "use strict";

    document.addEventListener("DOMContentLoaded", function () {
        // ---- Scroll reveal ----
        var revealTargets = document.querySelectorAll(".reveal-on-scroll");
        if (revealTargets.length && "IntersectionObserver" in window) {
            var observer = new IntersectionObserver(function (entries) {
                entries.forEach(function (entry) {
                    if (entry.isIntersecting) {
                        entry.target.classList.add("is-visible");
                        observer.unobserve(entry.target);
                    }
                });
            }, { threshold: 0.12, rootMargin: "0px 0px -40px 0px" });

            revealTargets.forEach(function (el) {
                observer.observe(el);
            });
        } else {
            revealTargets.forEach(function (el) {
                el.classList.add("is-visible");
            });
        }

        // ---- Navbar elevation on scroll ----
        var navbar = document.querySelector(".av-navbar");
        if (navbar) {
            var updateNav = function () {
                if (window.scrollY > 12) {
                    navbar.classList.add("is-elevated");
                } else {
                    navbar.classList.remove("is-elevated");
                }
            };
            updateNav();
            window.addEventListener("scroll", updateNav, { passive: true });
        }

        // ---- Number count-up: <span class="count-up" data-target="42">0</span> ----
        var counters = document.querySelectorAll(".count-up[data-target]");
        if (counters.length) {
            var animateCounter = function (el) {
                var target = parseInt(el.getAttribute("data-target"), 10) || 0;
                var duration = 1100;
                var start = null;
                var easeOutExpo = function (t) {
                    return t === 1 ? 1 : 1 - Math.pow(2, -10 * t);
                };
                function step(timestamp) {
                    if (!start) start = timestamp;
                    var progress = Math.min((timestamp - start) / duration, 1);
                    var value = Math.round(easeOutExpo(progress) * target);
                    el.textContent = value;
                    if (progress < 1) {
                        window.requestAnimationFrame(step);
                    } else {
                        el.textContent = target;
                    }
                }
                window.requestAnimationFrame(step);
            };

            if ("IntersectionObserver" in window) {
                var counterObserver = new IntersectionObserver(function (entries) {
                    entries.forEach(function (entry) {
                        if (entry.isIntersecting) {
                            animateCounter(entry.target);
                            counterObserver.unobserve(entry.target);
                        }
                    });
                }, { threshold: 0.4 });
                counters.forEach(function (el) { counterObserver.observe(el); });
            } else {
                counters.forEach(function (el) { el.textContent = el.getAttribute("data-target"); });
            }
        }

        // ---- Vote / score micro-bars: animate width in once visible ----
        var bars = document.querySelectorAll(".vote-bar-fill[data-width]");
        if (bars.length) {
            if ("IntersectionObserver" in window) {
                var barObserver = new IntersectionObserver(function (entries) {
                    entries.forEach(function (entry) {
                        if (entry.isIntersecting) {
                            entry.target.style.width = entry.target.getAttribute("data-width") + "%";
                            barObserver.unobserve(entry.target);
                        }
                    });
                }, { threshold: 0.2 });
                bars.forEach(function (el) { barObserver.observe(el); });
            } else {
                bars.forEach(function (el) { el.style.width = el.getAttribute("data-width") + "%"; });
            }
        }

        // ---- 3D tilt on hero seal + cards (mouse-tracked perspective) ----
        var reduceMotion = window.matchMedia && window.matchMedia("(prefers-reduced-motion: reduce)").matches;
        if (!reduceMotion) {
            var tiltTargets = document.querySelectorAll(".hero-seal"); // cards no longer tilt
            tiltTargets.forEach(function (el) {
                el.classList.add("tilt-3d");
                if (getComputedStyle(el).position === "static") {
                    el.style.position = "relative";
                }
                var shine = document.createElement("span");
                shine.className = "tilt-3d-shine";
                el.appendChild(shine);

                var strength = el.classList.contains("hero-seal") ? 22 : 8;
                el.addEventListener("mousemove", function (e) {
                    var rect = el.getBoundingClientRect();
                    var px = (e.clientX - rect.left) / rect.width;
                    var py = (e.clientY - rect.top) / rect.height;
                    var rx = (0.5 - py) * strength;
                    var ry = (px - 0.5) * strength;
                    el.style.transform = "perspective(900px) rotateX(" + rx.toFixed(2) + "deg) rotateY(" + ry.toFixed(2) + "deg) translateZ(4px)";
                    shine.style.setProperty("--mx", (px * 100).toFixed(1) + "%");
                    shine.style.setProperty("--my", (py * 100).toFixed(1) + "%");
                });
                el.addEventListener("mouseleave", function () {
                    el.style.transform = "";
                });
            });

            // ---- Hero orb parallax (mouse-driven depth) ----
            var hero = document.querySelector(".av-hero");
            var orbs = document.querySelectorAll(".av-hero .hero-orb");
            if (hero && orbs.length) {
                hero.addEventListener("mousemove", function (e) {
                    var rect = hero.getBoundingClientRect();
                    var px = (e.clientX - rect.left) / rect.width - 0.5;
                    var py = (e.clientY - rect.top) / rect.height - 0.5;
                    orbs.forEach(function (orb, i) {
                        var depth = (i + 1) * 14;
                        orb.style.transform = "translate3d(" + (px * depth) + "px," + (py * depth) + "px,0)";
                    });
                });
                hero.addEventListener("mouseleave", function () {
                    orbs.forEach(function (orb) { orb.style.transform = ""; });
                });
            }
        }

        // ---- Banner carousel: 3D cube-style directional transition ----
        var bannerCarousel = document.getElementById("bannerCarousel");
        if (bannerCarousel) {
            bannerCarousel.addEventListener("slide.bs.carousel", function (e) {
                bannerCarousel.classList.remove("rotate-next", "rotate-prev");
                bannerCarousel.classList.add(e.direction === "left" ? "rotate-next" : "rotate-prev");
            });
        }

        // ---- Click ripple feedback on every .btn ----
        document.addEventListener("click", function (e) {
            var btn = e.target.closest(".btn");
            if (!btn) return;
            var rect = btn.getBoundingClientRect();
            var size = Math.max(rect.width, rect.height);
            var ripple = document.createElement("span");
            ripple.className = "ripple";
            ripple.style.width = ripple.style.height = size + "px";
            ripple.style.left = (e.clientX - rect.left - size / 2) + "px";
            ripple.style.top = (e.clientY - rect.top - size / 2) + "px";
            btn.appendChild(ripple);
            window.setTimeout(function () {
                ripple.remove();
            }, 650);
        });
    });
})();
