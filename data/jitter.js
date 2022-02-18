const JITTER = 200;

module.exports = (req, res, next) =>
    setTimeout(next, Math.floor(Math.random() * Math.floor(JITTER)));
