import subprocess
def subprocess_run(command):
    logging.info(command)
    p = subprocess.Popen(command, shell=True, stdin=subprocess.PIPE,
                         stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    out, err = p.communicate(b"' stdin")
    cd = p.returncode
    return out

if __name__ == '__main__':
    out = subprocess_run(sys.argv[1])
    print(out)
